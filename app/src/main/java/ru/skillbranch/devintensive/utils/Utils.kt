package ru.skillbranch.devintensive.utils


object Utils {
    private val fullNameRegex = "^[a-zа-я'-]+ [a-zа-я,.'-]+.*".toRegex()
    private val nameRegex = "^[a-zа-я'-]+".toRegex()
    private val repRegex = "^(http(s)?://|www.|http(s)?://www.)?github.com/(?!(${getRegexExceptions()}))[A-Za-z\\d](?:[A-Za-z\\d]|-(?=[A-Za-z\\d])){0,38}(/)?\$".toRegex()

    private val mapTranslit: HashMap<String, String> = hashMapOf(
        "а" to "a",
        "б" to "b",
        "в" to "v",
        "г" to "g",
        "д" to "d",
        "е" to "e",
        "ё" to "e",
        "ж" to "zh",
        "з" to "z",
        "и" to "i",
        "й" to "i",
        "к" to "k",
        "л" to "l",
        "м" to "m",
        "н" to "n",
        "о" to "o",
        "п" to "p",
        "р" to "r",
        "с" to "s",
        "т" to "t",
        "у" to "u",
        "ф" to "f",
        "х" to "h",
        "ц" to "c",
        "ч" to "ch",
        "ш" to "sh",
        "щ" to "sh",
        "ъ" to "",
        "ы" to "i",
        "ь" to "",
        "э" to "e",
        "ю" to "yu",
        "я" to "ya")

    fun parseFullName(fullName: String?) : Pair<String?, String?> {
        val parts : List<String?>? =
            when(namePattern(fullName)) {
                MatchName.FULL_MATCH -> fullName?.split(" ")
                MatchName.PART_MATCH -> listOf("$fullName", null)
                MatchName.NO_MATCH -> null
            }

        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)
        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " ") =
        payload
            .split("")
            .filter { it != "" }
            .map { s ->
                when {
                    s == " " -> divider
                    s.toCharArray()[0].isUpperCase() && mapTranslit.containsKey(s.toLowerCase()) -> mapTranslit[s.toLowerCase()]?.capitalize()
                    mapTranslit.containsKey(s) -> mapTranslit[s]
                    else -> s
                }

            }
            .joinToString("")

    fun toInitials(firstName: String?, lastName: String?) : String? {
        val fullName = ((firstName ?: "") + " " + (lastName ?: "")).trim()
        return when(namePattern(fullName)) {
            MatchName.FULL_MATCH -> "${firstName!!.toCharArray()[0].toUpperCase()}${lastName!!.toCharArray()[0].toUpperCase()}"
            MatchName.PART_MATCH -> "${fullName.toCharArray()[0].toUpperCase()}"
            MatchName.NO_MATCH -> null
        }
    }

    fun validateRepository(repoString: String) : Boolean = repoString.matches(repRegex)

    fun getRegexExceptions(): String {
        val exceptions = arrayOf(
            "enterprise", "features", "topics", "collections", "trending", "events", "marketplace", "pricing",
            "nonprofit", "customer-stories", "security", "login", "join"
        )
        return exceptions
            .joinToString("[/]?$|","", "[/]?$")
    }

    private fun namePattern(fullName: String?) : MatchName =
                                                            when {
                                                                fullName == null -> MatchName.NO_MATCH
                                                                nameRegex.matches(fullName.toLowerCase().subSequence(0, fullName.length)) -> MatchName.PART_MATCH
                                                                fullNameRegex.matches(fullName.toLowerCase().subSequence(0, fullName.length)) -> MatchName.FULL_MATCH
                                                                else -> MatchName.NO_MATCH
                                                            }

    private enum class MatchName {
        FULL_MATCH,
        PART_MATCH,
        NO_MATCH
    }
}