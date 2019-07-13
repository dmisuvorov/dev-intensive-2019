package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion() : String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String) : Pair<String, Triple<Int, Int, Int>> =
            when {
                question.answers.contains(answer) -> {
                    question = question.nextQuestion()
                    "Отлично - ты справился\n${question.question}" to status.color
                }
                !question.validate(answer) -> {
                    question.validatedString() to status.color
                }
                else -> {
                    val allNewString : String = if (status == Status.CRITICAL) {
                        question = Question.NAME
                        ". Давай все по новой"
                    } else {
                        ""
                    }
                    status = status.nextStatus()
                    "Это неправильный ответ$allNewString\n${question.question}" to status.color
                }
            }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)) ,
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0)) ;

        fun nextStatus(): Status =
                if (ordinal < values().lastIndex) {
                    values()[ordinal + 1]
                } else {
                    values()[0]
                }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("Бендер", "Bender")) {
            override fun validate(answer: String): Boolean = if (answer.isNotEmpty()) answer[0].isUpperCase() else false

            override fun validatedString(): String = "Имя должно начинаться с заглавной буквы"

            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun validate(answer: String): Boolean = if (answer.isNotEmpty()) answer[0].isLowerCase() else false

            override fun validatedString(): String = "Профессия должна начинаться со строчной буквы"

            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun validate(answer: String): Boolean = !"\\d".toRegex().containsMatchIn(answer)

            override fun validatedString(): String = "Материал не должен содержать цифр"

            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun validate(answer: String): Boolean = "\\d+".toRegex().matches(answer)

            override fun validatedString(): String = "Год моего рождения должен содержать только цифры"

            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun validate(answer: String): Boolean = "\\d{7}".toRegex().matches(answer)

            override fun validatedString(): String = "Серийный номер содержит только цифры, и их 7"

            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun validate(answer: String): Boolean = false

            override fun validatedString(): String = IDLE.question

            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question

        abstract fun validate(answer: String): Boolean

        abstract fun validatedString(): String
    }
}