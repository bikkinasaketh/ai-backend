package com.aiinterview.aiinterviewbackend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AiEvaluationService {

    private final Client client;

    /*
     * Current stable Gemini Flash model.
     */
    private static final String MODEL =
            "gemini-3.6-flash";


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AiEvaluationService(
            @Value("${gemini.api-key}") String apiKey
    ) {

        if (apiKey == null || apiKey.isBlank()) {

            throw new IllegalStateException(
                    "GEMINI_API_KEY is not configured"
            );
        }

        client = Client.builder()
                .apiKey(apiKey)
                .build();
    }


    // =========================================================
    // SINGLE ANSWER EVALUATION
    // =========================================================

    public EvaluationResult evaluateAnswer(
            String topic,
            String difficulty,
            String question,
            String answer
    ) {

        if (answer == null || answer.isBlank()) {

            answer = "[No answer provided]";
        }


        String prompt = """
                You are an expert technical interviewer and
                personal interview coach.

                Topic:
                %s

                Difficulty:
                %s

                Interview Question:
                %s

                Candidate Answer:
                %s

                Evaluate the candidate answer based on:

                1. Technical correctness
                2. Understanding
                3. Completeness
                4. Clarity
                5. Interview readiness

                Do not give a high score just because the answer
                contains some correct keywords.

                Identify:

                - What the candidate answered correctly
                - What is missing or incorrect
                - What the candidate should learn next
                - How the candidate could answer better

                Return EXACTLY:

                SCORE: <number>

                FEEDBACK: <short overall evaluation>

                CORRECT:
                - <correct point>
                - <correct point>

                IMPROVEMENTS:
                - <improvement>
                - <improvement>

                LEARN_NEXT:
                - <topic to learn>
                - <topic to learn>

                BETTER_ANSWER:
                <ideal interview answer>

                Rules:

                SCORE must be between 0 and 100.

                Keep feedback concise but useful.

                Do not invent facts about the candidate.

                If the answer is completely wrong,
                clearly explain it.

                If the answer is correct but incomplete,
                clearly mention what is missing.

                BETTER_ANSWER should be suitable for a real job interview.
                """.formatted(
                topic,
                difficulty,
                question,
                answer
        );


        try {

            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL,
                            prompt,
                            null
                    );


            String output =
                    response.text();


            if (output == null ||
                    output.isBlank()) {

                throw new RuntimeException(
                        "Gemini returned an empty response"
                );
            }


            return parseDetailedResult(
                    output
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    buildGeminiErrorMessage(
                            "evaluation",
                            e
                    ),
                    e
            );
        }
    }


    // =========================================================
    // BATCH ANSWER EVALUATION
    //
    // ONE GEMINI REQUEST FOR ALL QUESTIONS
    // =========================================================

    public List<EvaluationResult> evaluateAnswers(
            String topic,
            String difficulty,
            List<String> questions,
            List<String> answers
    ) {

        if (questions == null ||
                questions.isEmpty()) {

            throw new IllegalArgumentException(
                    "Questions are required"
            );
        }


        if (answers == null ||
                answers.isEmpty()) {

            throw new IllegalArgumentException(
                    "Answers are required"
            );
        }


        if (questions.size() != answers.size()) {

            throw new IllegalArgumentException(
                    "Questions and answers count must match"
            );
        }


        StringBuilder prompt =
                new StringBuilder();


        prompt.append("""
                You are an expert technical interviewer and
                personal interview coach.

                Evaluate ALL candidate answers.

                Topic:
                """);


        prompt.append(topic);


        prompt.append("""

                Difficulty:
                """);


        prompt.append(difficulty);


        prompt.append("""

                Evaluate every candidate answer based on:

                1. Technical correctness
                2. Understanding
                3. Completeness
                4. Clarity
                5. Interview readiness

                IMPORTANT RULES:

                - Evaluate every question.
                - Keep exactly the same order.
                - Do not skip questions.
                - Do not combine questions.
                - Do not combine answers.
                - Give every question its own score.
                - SCORE must be between 0 and 100.
                - Do not give a high score just because keywords exist.
                - Judge whether the candidate actually understands the concept.
                - Keep feedback useful and concise.

                For every question identify:

                1. What the candidate answered correctly.
                2. What is missing or incorrect.
                3. What the candidate should learn next.
                4. A better interview answer.

                Return EXACTLY this format:

                QUESTION 1

                SCORE: <number>

                FEEDBACK: <short overall evaluation>

                CORRECT:
                - <correct point>
                - <correct point>

                IMPROVEMENTS:
                - <improvement>
                - <improvement>

                LEARN_NEXT:
                - <topic>
                - <topic>

                BETTER_ANSWER:
                <ideal interview answer>


                QUESTION 2

                SCORE: <number>

                FEEDBACK: <short overall evaluation>

                CORRECT:
                - <correct point>
                - <correct point>

                IMPROVEMENTS:
                - <improvement>
                - <improvement>

                LEARN_NEXT:
                - <topic>
                - <topic>

                BETTER_ANSWER:
                <ideal interview answer>

                Continue exactly the same format
                for every question.

                Do not add an introduction.

                Do not add a conclusion.

                Do not add an overall score.

                """);


        // =====================================================
        // ADD QUESTIONS + ANSWERS
        // =====================================================

        for (int i = 0;
             i < questions.size();
             i++) {

            String question =
                    questions.get(i);


            if (question == null ||
                    question.isBlank()) {

                question =
                        "[Question unavailable]";
            }


            String answer =
                    answers.get(i);


            if (answer == null ||
                    answer.isBlank()) {

                answer =
                        "[No answer provided]";
            }


            prompt.append(
                    "\nQUESTION "
                            + (i + 1)
                            + "\n"
            );


            prompt.append(
                    "Question: "
                            + question
                            + "\n"
            );


            prompt.append(
                    "Candidate Answer: "
                            + answer
                            + "\n"
            );
        }


        try {

            // =================================================
            // ONE GEMINI REQUEST
            // =================================================

            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL,
                            prompt.toString(),
                            null
                    );


            String output =
                    response.text();


            if (output == null ||
                    output.isBlank()) {

                throw new RuntimeException(
                        "Gemini returned an empty response"
                );
            }


            return parseBatchResults(
                    output,
                    questions.size()
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    buildGeminiErrorMessage(
                            "batch evaluation",
                            e
                    ),
                    e
            );
        }
    }


    // =========================================================
    // QUESTION GENERATION
    // =========================================================

    public List<String> generateQuestions(
            String topic,
            String difficulty,
            int numberOfQuestions
    ) {

        if (topic == null ||
                topic.isBlank()) {

            throw new IllegalArgumentException(
                    "Topic is required"
            );
        }


        if (difficulty == null ||
                difficulty.isBlank()) {

            throw new IllegalArgumentException(
                    "Difficulty is required"
            );
        }


        if (numberOfQuestions < 1 ||
                numberOfQuestions > 20) {

            throw new IllegalArgumentException(
                    "Number of questions must be between 1 and 20"
            );
        }


        String prompt = """
                You are an expert technical interviewer.

                Generate exactly %d interview questions.

                Topic:
                %s

                Difficulty:
                %s

                Requirements:

                1. Questions must be relevant to the selected topic.
                2. Questions must match the selected difficulty.
                3. Every question must be unique.
                4. Questions must be suitable for a real job interview.
                5. Do not provide answers.
                6. Do not provide explanations.
                7. Do not number the questions.
                8. Return one question per line.
                9. Do not add an introduction.
                10. Do not add a conclusion.

                Difficulty rules:

                EASY:
                Basic concepts and beginner-friendly questions.

                MEDIUM:
                Practical, conceptual and moderately challenging
                interview questions.

                HARD:
                Advanced concepts, real-world scenarios,
                problem solving and deeper technical questions.

                Return ONLY the questions.
                """.formatted(
                numberOfQuestions,
                topic,
                difficulty
        );


        try {

            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL,
                            prompt,
                            null
                    );


            String output =
                    response.text();


            if (output == null ||
                    output.isBlank()) {

                throw new RuntimeException(
                        "Gemini returned no questions"
                );
            }


            return parseQuestions(
                    output,
                    numberOfQuestions
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    buildGeminiErrorMessage(
                            "question generation",
                            e
                    ),
                    e
            );
        }
    }


    // =========================================================
    // PARSE BATCH RESULTS
    // =========================================================

    private List<EvaluationResult> parseBatchResults(
            String output,
            int expectedCount
    ) {

        List<EvaluationResult> results =
                new ArrayList<>();


        String normalized =
                output.replace(
                        "\r\n",
                        "\n"
                );


        String[] blocks =
                normalized.split(
                        "(?i)\\bQUESTION\\s+\\d+\\b"
                );


        for (String block : blocks) {

            if (block == null ||
                    block.isBlank()) {

                continue;
            }


            String upper =
                    block.toUpperCase();


            if (!upper.contains("SCORE:")) {

                continue;
            }


            EvaluationResult result =
                    parseDetailedResult(
                            block
                    );


            results.add(result);
        }


        if (results.size() != expectedCount) {

            throw new RuntimeException(
                    "Gemini returned "
                            + results.size()
                            + " evaluations, but "
                            + expectedCount
                            + " were expected."
                            + "\n\nGemini output:\n"
                            + output
            );
        }


        return results;
    }


    // =========================================================
    // PARSE DETAILED RESULT
    // =========================================================

    private EvaluationResult parseDetailedResult(
            String output
    ) {

        int score =
                extractScore(output);


        String feedback =
                extractSection(
                        output,
                        "FEEDBACK:",
                        "CORRECT:"
                );


        List<String> correctPoints =
                extractListSection(
                        output,
                        "CORRECT:",
                        "IMPROVEMENTS:"
                );


        List<String> improvements =
                extractListSection(
                        output,
                        "IMPROVEMENTS:",
                        "LEARN_NEXT:"
                );


        List<String> learnNext =
                extractListSection(
                        output,
                        "LEARN_NEXT:",
                        "BETTER_ANSWER:"
                );


        String betterAnswer =
                extractSection(
                        output,
                        "BETTER_ANSWER:",
                        null
                );


        // =====================================================
        // FALLBACK VALUES
        // =====================================================

        if (feedback.isBlank()) {

            feedback =
                    "No detailed feedback was provided.";
        }


        if (correctPoints.isEmpty()) {

            correctPoints =
                    Collections.singletonList(
                            "Review the answer against the interview question."
                    );
        }


        if (improvements.isEmpty()) {

            improvements =
                    Collections.singletonList(
                            "Add clearer technical explanation and examples."
                    );
        }


        if (learnNext.isEmpty()) {

            learnNext =
                    Collections.singletonList(
                            "Review the core concepts related to this question."
                    );
        }


        if (betterAnswer.isBlank()) {

            betterAnswer =
                    "Give a clear definition, explain the main concept, "
                            + "and provide a simple practical example.";
        }


        return new EvaluationResult(
                score,
                feedback,
                correctPoints,
                improvements,
                learnNext,
                betterAnswer
        );
    }


    // =========================================================
    // EXTRACT SCORE
    // =========================================================

    private int extractScore(
            String text
    ) {

        for (String line :
                text.split("\\R")) {

            String lineText =
                    line.trim();


            if (lineText
                    .toUpperCase()
                    .startsWith("SCORE:")) {

                String value =
                        lineText
                                .substring(6)
                                .trim()
                                .replaceAll(
                                        "[^0-9]",
                                        ""
                                );


                if (!value.isBlank()) {

                    try {

                        int score =
                                Integer.parseInt(
                                        value
                                );


                        return Math.max(
                                0,
                                Math.min(
                                        100,
                                        score
                                )
                        );


                    } catch (
                            NumberFormatException ignored
                    ) {

                        return 0;
                    }
                }
            }
        }


        return 0;
    }


    // =========================================================
    // EXTRACT TEXT SECTION
    // =========================================================

    private String extractSection(
            String output,
            String startMarker,
            String endMarker
    ) {

        if (output == null ||
                output.isBlank()) {

            return "";
        }


        String upper =
                output.toUpperCase();


        String upperStart =
                startMarker.toUpperCase();


        int start =
                upper.indexOf(
                        upperStart
                );


        if (start == -1) {

            return "";
        }


        start +=
                startMarker.length();


        int end;


        if (endMarker == null) {

            end =
                    output.length();

        } else {

            end =
                    upper.indexOf(
                            endMarker.toUpperCase(),
                            start
                    );


            if (end == -1) {

                end =
                        output.length();
            }
        }


        if (end < start) {

            return "";
        }


        return output
                .substring(
                        start,
                        end
                )
                .trim();
    }


    // =========================================================
    // EXTRACT BULLET LIST
    // =========================================================

    private List<String> extractListSection(
            String output,
            String startMarker,
            String endMarker
    ) {

        String section =
                extractSection(
                        output,
                        startMarker,
                        endMarker
                );


        if (section.isBlank()) {

            return new ArrayList<>();
        }


        List<String> result =
                new ArrayList<>();


        for (String line :
                section.split("\\R")) {

            String item =
                    line.trim();


            if (item.isBlank()) {

                continue;
            }


            // Remove bullet
            item =
                    item.replaceFirst(
                            "^[-*•]\\s*",
                            ""
                    );


            // Remove numbering
            item =
                    item.replaceFirst(
                            "^\\d+[.)]\\s*",
                            ""
                    );


            item =
                    item.trim();


            if (!item.isBlank()) {

                result.add(item);
            }
        }


        return result;
    }


    // =========================================================
    // PARSE QUESTIONS
    // =========================================================

    private List<String> parseQuestions(
            String output,
            int expectedCount
    ) {

        List<String> questions =
                new ArrayList<>();


        String[] lines =
                output.split("\\R");


        for (String line :
                lines) {

            String question =
                    line.trim();


            if (question.isBlank()) {

                continue;
            }


            // Remove numbering
            question =
                    question.replaceFirst(
                            "^\\d+[.)]\\s*",
                            ""
                    );


            // Remove bullets
            question =
                    question.replaceFirst(
                            "^[-*•]\\s*",
                            ""
                    );


            question =
                    question.trim();


            if (!question.isBlank()) {

                questions.add(question);
            }
        }


        if (questions.isEmpty()) {

            throw new RuntimeException(
                    "Could not parse questions from Gemini response"
            );
        }


        if (questions.size() > expectedCount) {

            return new ArrayList<>(
                    questions.subList(
                            0,
                            expectedCount
                    )
            );
        }


        if (questions.size() < expectedCount) {

            throw new RuntimeException(
                    "Gemini returned "
                            + questions.size()
                            + " questions, but "
                            + expectedCount
                            + " were expected."
            );
        }


        return questions;
    }


    // =========================================================
    // GEMINI ERROR MESSAGE
    // =========================================================

    private String buildGeminiErrorMessage(
            String operation,
            Exception exception
    ) {

        String message =
                exception.getMessage();


        if (message == null) {

            message =
                    exception.toString();
        }


        String lower =
                message.toLowerCase();


        // =====================================================
        // QUOTA / RATE LIMIT
        // =====================================================

        if (lower.contains("429") ||
                lower.contains("quota") ||
                lower.contains("rate limit") ||
                lower.contains("resource_exhausted")) {

            return
                    "Gemini quota exceeded during "
                            + operation
                            + ". "
                            + "Your Gemini API project has reached "
                            + "its current quota/rate limit. "
                            + "Please wait for the quota reset "
                            + "or use a Gemini API project/tier "
                            + "with available quota.";
        }


        // =====================================================
        // API KEY
        // =====================================================

        if (lower.contains("api key") ||
                lower.contains("api_key") ||
                lower.contains("unauthorized") ||
                lower.contains("401")) {

            return
                    "Gemini API key error during "
                            + operation
                            + ". "
                            + "Check gemini.api-key in your "
                            + "environment configuration.";
        }


        // =====================================================
        // PERMISSION
        // =====================================================

        if (lower.contains("permission") ||
                lower.contains("403")) {

            return
                    "Gemini permission error during "
                            + operation
                            + ". "
                            + "Check whether the Gemini API "
                            + "is enabled for your project.";
        }


        // =====================================================
        // MODEL ERROR
        // =====================================================

        if (lower.contains("model") &&
                (
                        lower.contains("not found") ||
                        lower.contains("unsupported")
                )) {

            return
                    "Gemini model error during "
                            + operation
                            + ". "
                            + "Check that model "
                            + MODEL
                            + " is available for your API project.";
        }


        // =====================================================
        // GENERIC ERROR
        // =====================================================

        return
                "Gemini "
                        + operation
                        + " failed: "
                        + message;
    }


    // =========================================================
    // EVALUATION RESULT
    // =========================================================

    public static class EvaluationResult {

        private final int score;

        private final String feedback;

        private final List<String> correctPoints;

        private final List<String> improvements;

        private final List<String> learnNext;

        private final String betterAnswer;


        public EvaluationResult(
                int score,
                String feedback,
                List<String> correctPoints,
                List<String> improvements,
                List<String> learnNext,
                String betterAnswer
        ) {

            this.score =
                    Math.max(
                            0,
                            Math.min(
                                    100,
                                    score
                            )
                    );


            this.feedback =
                    feedback != null
                            ? feedback
                            : "";


            this.correctPoints =
                    correctPoints != null
                            ? correctPoints
                            : new ArrayList<>();


            this.improvements =
                    improvements != null
                            ? improvements
                            : new ArrayList<>();


            this.learnNext =
                    learnNext != null
                            ? learnNext
                            : new ArrayList<>();


            this.betterAnswer =
                    betterAnswer != null
                            ? betterAnswer
                            : "";
        }


        public int getScore() {

            return score;
        }


        public String getFeedback() {

            return feedback;
        }


        public List<String> getCorrectPoints() {

            return correctPoints;
        }


        public List<String> getImprovements() {

            return improvements;
        }


        public List<String> getLearnNext() {

            return learnNext;
        }


        public String getBetterAnswer() {

            return betterAnswer;
        }
    }
}