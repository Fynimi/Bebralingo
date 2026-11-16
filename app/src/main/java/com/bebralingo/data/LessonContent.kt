package com.bebralingo.data

import androidx.compose.ui.graphics.Color
import com.bebralingo.model.Lesson
import com.bebralingo.model.LessonBlock

/**
 * Весь учебный контент тренажёра: 5 блоков по этапам освоения клавиатуры.
 * Тексты подобраны так, чтобы блок использовал только освоенные к нему клавиши.
 */
object LessonContent {

    val blocks: List<LessonBlock> = listOf(
        LessonBlock(
            id = "b1",
            title = "Основной ряд",
            description = "ФЫВА ОЛДЖ — базовые клавиши",
            accent = Color(0xFF58CC02),
            lessons = listOf(
                Lesson(
                    id = "b1l1", blockId = "b1",
                    title = "А и О", subtitle = "Частые гласные",
                    targetText = "ао аоао оа оаоа аоаоао",
                    focusKeys = "ао"
                ),
                Lesson(
                    id = "b1l2", blockId = "b1",
                    title = "Ф и Ж", subtitle = "Крайние клавиши",
                    targetText = "аф жа оф фа жаж жаф афа",
                    focusKeys = "аофж"
                ),
                Lesson(
                    id = "b1l3", blockId = "b1",
                    title = "Весь ряд слева", subtitle = "Ф Ы В А",
                    targetText = "фыва ыва аыв вва ыаф аывфыв",
                    focusKeys = "фыва"
                ),
                Lesson(
                    id = "b1l4", blockId = "b1",
                    title = "Весь ряд справа", subtitle = "О Л Д Ж",
                    targetText = "олдж лджд одо жло долдоло",
                    focusKeys = "олдж"
                ),
                Lesson(
                    id = "b1l5", blockId = "b1",
                    title = "Слоги и слова", subtitle = "Комбинации ряда",
                    targetText = "довод вода повар лад олово жала долглод",
                    focusKeys = "фываолдж"
                )
            )
        ),
        LessonBlock(
            id = "b2",
            title = "Верхний ряд",
            description = "ЙЦУКЕН ГШЩЗХЪ — тянемся вверх",
            accent = Color(0xFF1CB7EB),
            lessons = listOf(
                Lesson(
                    id = "b2l1", blockId = "b2",
                    title = "Й Ц У К", subtitle = "Левая половина",
                    targetText = "йку цук йук кцы ууц ккуй",
                    focusKeys = "йцу"
                ),
                Lesson(
                    id = "b2l2", blockId = "b2",
                    title = "Е Н Г Ш", subtitle = "Центр ряда",
                    targetText = "еген шег нге шеен ггне",
                    focusKeys = "енгш"
                ),
                Lesson(
                    id = "b2l3", blockId = "b2",
                    title = "Щ З Х Ъ", subtitle = "Правый край",
                    targetText = "щзхъ зщхъ ъхзщ щщх ъзх",
                    focusKeys = "щзхъ"
                ),
                Lesson(
                    id = "b2l4", blockId = "b2",
                    title = "Слова верхнего ряда", subtitle = "Верхний ряд + основной",
                    targetText = "щека укол кран река ранг чек",
                    focusKeys = "йцукенгшщзхъ"
                ),
                Lesson(
                    id = "b2l5", blockId = "b2",
                    title = "Микс рядов", subtitle = "Верх + основа",
                    targetText = "щенок утка галка чердак жук пенал",
                    focusKeys = "верхний"
                )
            )
        ),
        LessonBlock(
            id = "b3",
            title = "Нижний ряд",
            description = "ЯЧСМИТЬБЮ — самое дальнее движение",
            accent = Color(0xFFFF9600),
            lessons = listOf(
                Lesson(
                    id = "b3l1", blockId = "b3",
                    title = "Я Ч С М", subtitle = "Левая половина",
                    targetText = "ямч смя чья ммс ячс мяч",
                    focusKeys = "ячсм"
                ),
                Lesson(
                    id = "b3l2", blockId = "b3",
                    title = "И Т Ь Б Ю", subtitle = "Правая половина",
                    targetText = "ить ють бить тибь ююб тить",
                    focusKeys = "итьбю"
                ),
                Lesson(
                    id = "b3l3", blockId = "b3",
                    title = "Слова нижнего ряда", subtitle = "Все три ряда",
                    targetText = "мяч мышь честь стая быт юла",
                    focusKeys = "яЧСМИТЬБЮ"
                ),
                Lesson(
                    id = "b3l4", blockId = "b3",
                    title = "Полная клавиатура", subtitle = "Левая половина",
                    targetText = "чемодан сметана печка тучка свеча",
                    focusKeys = "полный"
                ),
                Lesson(
                    id = "b3l5", blockId = "b3",
                    title = "Микс всех рядов", subtitle = "Правая половина",
                    targetText = "любить змея ёжик чистюля ястреб",
                    focusKeys = "полный"
                )
            )
        ),
        LessonBlock(
            id = "b4",
            title = "Заглавные, цифры, знаки",
            description = "Shift, верхний цифровой ряд и пунктуация",
            accent = Color(0xFFCE82FF),
            lessons = listOf(
                Lesson(
                    id = "b4l1", blockId = "b4",
                    title = "Заглавные буквы", subtitle = "Shift + буква",
                    targetText = "А О Ф Ж МЫ ЙЦУ КЕН Россия Москва",
                    focusKeys = "shift"
                ),
                Lesson(
                    id = "b4l2", blockId = "b4",
                    title = "Цифры", subtitle = "1234567890",
                    targetText = "12345 67890 10 20 100 1985 2026",
                    focusKeys = "цифры"
                ),
                Lesson(
                    id = "b4l3", blockId = "b4",
                    title = "Знаки препинания", subtitle = "Точка, запятая, тире",
                    targetText = "мама, папа, сын и дочь. дом - новый!",
                    focusKeys = ".,-"
                ),
                Lesson(
                    id = "b4l4", blockId = "b4",
                    title = "Предложения", subtitle = "Заглавная + точка",
                    targetText = "Наступила весна. Светит яркое солнце.",
                    focusKeys = "shift.,"
                )
            )
        ),
        LessonBlock(
            id = "b5",
            title = "Слова и тексты",
            description = "Реальные слова, связные тексты и скороговорки",
            accent = Color(0xFFFF4B4B),
            lessons = listOf(
                Lesson(
                    id = "b5l1", blockId = "b5",
                    title = "Частые слова", subtitle = "Топ-20 по русскому",
                    targetText = "и не что он с этот к как но быть один",
                    focusKeys = "слова"
                ),
                Lesson(
                    id = "b5l2", blockId = "b5",
                    title = "Простой текст", subtitle = "Короткие предложения",
                    targetText = "У Маши есть серый кот. Кот любит спать на диване.",
                    focusKeys = "текст"
                ),
                Lesson(
                    id = "b5l3", blockId = "b5",
                    title = "Скороговорка", subtitle = "Скорость и чёткость",
                    targetText = "Шла Саша по шоссе и сосала сушку. Карл у Клары украл кораллы.",
                    focusKeys = "текст"
                ),
                Lesson(
                    id = "b5l4", blockId = "b5",
                    title = "Панграмма", subtitle = "Все буквы алфавита",
                    targetText = "Съешь ещё этих мягких французских булок, да выпей же чаю.",
                    focusKeys = "текст"
                ),
                Lesson(
                    id = "b5l5", blockId = "b5",
                    title = "Связный текст", subtitle = "Финальный экзамен",
                    targetText = "Слепая печать - это навык. Тренируйся каждый день по чуть-чуть, и пальцы запомнят все клавиши.",
                    focusKeys = "текст"
                )
            )
        )
    )

    /** Плоский список всех уроков по порядку пути. */
    val allLessons: List<Lesson> = blocks.flatMap { it.lessons }

    /** Пул случайных текстов для спринтов. */
    val sprintTexts: List<String> = listOf(
        "Съешь ещё этих мягких французских булок, да выпей же чаю.",
        "Уронили мишку на пол, оторвали мишке лапу.",
        "В чае я чай не люблю, я люблю чай с сахаром.",
        "Шла Саша по шоссе и сосала сушку.",
        "Мой дядя самых честных правил, когда не в шутку занемог.",
        "Один робот собирал данные, другой читал книги, третий спал.",
        "Зима недаром злится, прошла её весна.",
        "Какое счастие гулять по летнему саду и дышать свежим воздухом.",
        "Надо учиться печатать вслепую, не глядя на клавиатуру.",
        "Каждый день понемногу даёт большой результат через месяц.",
        "Котёнок грелся на окошке и смотрел на снег за окном.",
        "Скорость приходит вместе с точностью, а не вместо неё.",
        "Мы любим русский язык за его красоту и богатство.",
        "Тысяча слов, десять пальцев, одна клавиатура и немного практики.",
        "Утро началось с кофе, свежего хлеба и хорошей книги."
    )

    fun findLesson(id: String): Lesson? = allLessons.firstOrNull { it.id == id }

    fun findBlock(id: String): LessonBlock? = blocks.firstOrNull { it.id == id }
}
