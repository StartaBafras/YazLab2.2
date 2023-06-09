import com.soywiz.korge.Korge
import com.soywiz.korge.box2d.*
import com.soywiz.korge.debug.*
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.random.*
import kotlinx.coroutines.*
import org.jbox2d.dynamics.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random
import com.soywiz.korio.file.std.resourcesVfs


var scane_switcher = 0
lateinit var sceneContainer: SceneContainer
var score1 = 0
lateinit var scores : Sequence<String>
suspend fun main() = Korge(width = 512, height =1280, bgcolor = Colors["#2b2b2b"]) {
    sceneContainer = sceneContainer()
    sceneContainer.changeTo({ MyScene() })

}

class Scene2 : Scene() {
    lateinit var scoreText: Text // skor kutusunu tutacak değişken

    override suspend fun SContainer.sceneInit() {
        val font = resourcesVfs["BebasNeue-Regular.ttf"].readTtfFont()

        val bgScore= circle(80.0,  Colors["#75888c"]) {
            position(180.0, 370.0)
        }
        scoreText = text("$score1", textSize = 32.0).centerOn(bgScore)

        val  restartButton=  uiButton(width = 125.0, 105.0, text = "YENIDEN BASLAT") {
            position(200, 1000)
            onPress {
                scane_switcher = -1
                score1 = 0
            }

        }.also { it.colorMul = Colors.DEEPSKYBLUE }


        text("Oyun Bitti", textSize = 80.0,Colors.WHITE,font)
            .xy(125, 600)

        val scoreFile = resourcesVfs["scores.txt"]
        scores = scoreFile.readLines()
        scores += "$score1"

        val sortedScores = scores.filter { it.isNotEmpty() }
            .sortedByDescending { it.toInt() }
            .take(5)

        for ((index, score) in sortedScores.withIndex()) {
            text("${index+1}. Sıra     ${score}    Puan", textSize = 30.0, color = Colors.WHITE, font = font)
                .xy(125, 700 + index * 50)
        }



        withContext(Dispatchers.IO) {
            scoreFile.writeString(scores.joinToString("\n"))
        }


    }

    override suspend fun SContainer.sceneMain() {

        while (true)
        {
            if(scane_switcher == -1)
            {
                scane_switcher = 0
                sceneContainer.changeTo({ MyScene() })
                break
            }
            delay(1000)
        }
    }


}




class MyScene : Scene() {
    val destiny = 10.0
    val boxSize = 49.0
    lateinit var scoreText: Text // skor kutusunu tutacak değişken
    lateinit var InputText: Text
    var contText = "beyhan ;)"
    //var score1 = 0 // skoru tutacak değişken
    val contentInput = ArrayList<String>()
    var delayTime = 5000L

    override suspend fun SContainer.sceneInit() {

        val font = resourcesVfs["BebasNeue-Regular.ttf"].readTtfFont()
        graphics {
            it.position(0.0, 350.0)
            fill(Colors["#474748"]) {
                roundRect(0.0, 0.0, 512.0, 200.0, 5.0)
            }
        }
        val bgScore = circle(80.0, Colors["#75888c"]) {
            position(180.0, 370.0)
        }
        scoreText = text("$score1", textSize = 32.0).centerOn(bgScore)

        val bgInput = solidRect(412, 50, Colors.DARKGOLDENROD).position(0, 1050)
            .registerBodyWithFixture(
                type = BodyType.STATIC,
                friction = 0.99
            )
        InputText = text("$contText", textSize = 30.0, Colors.WHITE, font)
            .centerOn(bgInput)


    }







    override suspend fun SContainer.sceneMain() {

        val file = resourcesVfs["word_list.txt"]
        val lines = file.readLines()

        //val scoreFile = resourcesVfs["scores.txt"]
        //scores = scoreFile.readLines()


        val random1 = Random(0L)
        val font = resourcesVfs["BebasNeue-Regular.ttf"].readTtfFont()
        val cellSize = views.virtualWidth / 10.0
        val fieldSize = 50 + 8 * cellSize
        val leftIndent = (views.virtualWidth - fieldSize) / 2
        val topIndent = 150.0
        val charr = "ABCDEFGHIJKLMNOPRSTUVYZÇĞİÖŞÜ"
        val charrList = charr.split("").filter { it.isNotEmpty() }.toList() as ArrayList<String>

        val vovels = "AEIİOÖUÜ"
        val consonants = "BCÇDFGĞHJKLMNPRSŞTVYZ"
        val vovels_array = vovels.split("").filter { it.isNotEmpty() }.toList() as ArrayList<String>
        val consonants_array = consonants.split("").filter { it.isNotEmpty() }.toList() as ArrayList<String>
        var char_rate = 1
        var button_text = "A"

        var falseCount= 0
        var search_flag = 0

        val buttonList = arrayListOf<UIButton>()
        val screen_butons = arrayListOf<UIButton>()
        val allButton = ArrayList<MyObject>()


        charrList.shuffle()
        solidRect(512, 50, Colors.DARKGOLDENROD).position(0, 1050).registerBodyWithFixture(
            type = BodyType.STATIC,
            friction = 0.99
        ).visible=false

        graphics {
            it.position(0.0,550.0)
            fill(Colors["#cec0b2"]) {
                roundRect(0.0, 0.0, 512.0, 500.0, 5.0)
            }
        }

//      val bgScore= circle(80.0,  Colors["#75888c"]) {
//            position(180.0, 370.0)
//        }
//        text("", cellSize * 0.5, Colors.WHITE, ).centerOn(bgScore)

        //süs
        solidRect(240.0,7.0,  Colors.GOLD) {
            position(140.0, 550.0)
        }

        //direk
        for (i in 0..8)
            solidRect(3, 470, Colors.BLACK).position(2+i*63, 600).registerBodyWithFixture(
                type = BodyType.STATIC,
                friction = 0.99
            ).visible=false
        //silme butonu
        val  removeBttn=  uiButton(width = 105.0, 65.0, text = "") {
            position(-1, 1045)
            onPress {
                if (buttonList.isNotEmpty()) {
                    // Listenin son elemanını sil
                    for (k in 0..buttonList.size-1){

                        val btn = buttonList.removeAt(buttonList.size - 1)
                        val btn2 = allButton.find{it.button == btn}

                        //allButton.find{it.button == btn}!!.isClicked  -= 1

                        if (btn2 != null) {
                            btn2.isClicked  -= 1
                            if(btn2.isIce != 0) {
                                btn2.button.colorMul = Colors.BLUE
                            } else if (btn2.transformIce != 0) {
                                btn2.button.colorMul = Colors.DEEPSKYBLUE
                            }
                            else
                            {
                                btn2.button.colorMul = random1[random1[Colors.CYAN, Colors.WHITE], random1[Colors.YELLOW, Colors.CYAN]]
                            }
                        }


                    }
                }

                contentInput.clear()
                InputText.text = "${contentInput.joinToString(separator = "")}"

            }


        }.also { it.colorMul = Colors["#ff1552"] }
            .also { it.textColor = Colors.WHITE }
            .also { it.textSize =cellSize/1.5 }
        text("X", cellSize * 0.5, Colors.WHITE, font).centerOn(removeBttn)


        //onaylama butonu
        val onay=uiButton(width = 105.0, 65.0, text = "OK") {
            position(410, 1045)
            onPress {

                //val c_text = contentInput.joinToString(separator = "").lowercase()

                val c_text = turkishLowercase(contentInput.joinToString(separator = ""))
                for (line in lines) {
                    if (line.equals(c_text))
                    {
                        calculateWordPoints(contentInput.joinToString(separator = ""))
                        search_flag = 1
                    }
                }

                if(search_flag == 0)
                {
                    falseCount += 1
                }




                if(search_flag==1){ // Doğruysa buraya düşecek
                    if (buttonList.isNotEmpty()) {
                        // Listenin son elemanını sil
                        for (k in 0..buttonList.size-1){
                            val btn = buttonList.removeAt(buttonList.size - 1)
                            val btn2 =  allButton.find { it.button == btn }

                            if (btn2 != null) {
                                if(btn2.isIce != 2 && btn2.transformIce != 2) // Normal blok veya kullanılmış buz blok
                                {
                                    if(btn2.transformIce == 1) // Buz yenilerini etkileyecek
                                    {
                                        val i = ((btn2.button.x - 13) / (63)).coerceIn(0.0, 7.0).roundToInt()

                                        var targetButton = allButton.filter { abs(13 + i * 63 - it.button.x) < 30 }


                                        targetButton = targetButton.sortedBy { it.button.y }

                                        val index = targetButton.indexOf(btn2)

                                        if (index + 1  < targetButton.size)
                                        {
                                            if(index - 1 >= 0)
                                            {
                                                if(targetButton.get(index-1).isIce != 0)
                                                {
                                                    if(targetButton.get(index+1).isIce == 0 && targetButton.get(index+1).transformIce == 0)
                                                    {
                                                        targetButton.get(index+1).transformIce= 2
                                                        targetButton.get(index+1).button.colorMul = Colors.DEEPSKYBLUE
                                                    }
                                                }
                                            }

                                        }
                                        if(index - 1 >= 0 && (index + 1  < targetButton.size))
                                        {
                                            if(targetButton.get(index+1).isIce != 0)
                                            {
                                                if(targetButton.get(index-1).isIce == 0 && targetButton.get(index-1).transformIce == 0)
                                                {
                                                    targetButton.get(index-1).transformIce= 2
                                                    targetButton.get(index-1).button.colorMul = Colors.DEEPSKYBLUE
                                                }
                                            }
                                        }


                                    }

                                    btn.position(440.0,4004.0)
                                    //screen_butons.remove(btn)
                                    allButton.remove(btn2)

                                }
                                else
                                {
                                    if(btn2.isIce == 2)
                                    {
                                        btn2.isIce = 1
                                        btn2.button.colorMul = Colors.BLUE
                                        btn2.isClicked = 0
                                    }
                                    else
                                    {
                                        btn2.transformIce = 1
                                        btn2.button.colorMul = Colors.DEEPSKYBLUE
                                        btn2.isClicked = 0
                                    }
                                }
                            }

                            contentInput.clear()
                            InputText.text = "${contentInput.joinToString(separator = "")}"

                        }}
                }
                else {//??

                    //falseCount += 1
                    println(falseCount)
                    if (falseCount == 3) {
                        falseCount = 0
                        for (i in 0..7) {
                            if(char_rate % 2 == 0)
                            {
                                val  charRand = Random.nextInt(0,vovels_array.size)
                                button_text = vovels_array[charRand]
                            }
                            else
                            {
                                val  charRand = Random.nextInt(0,consonants_array.size)
                                button_text = consonants_array[charRand]
                                if(char_rate == 5)
                                {
                                    char_rate = 1
                                }
                            }
                            char_rate += 1

                            val targetButton = allButton.filter { abs(13 + i * 63 - it.button.x) < 30 }
                                .minByOrNull { it.button.y }

                            val newb=  uiButton(width = boxSize, boxSize, text = button_text) {
                                position(13+ i * 63, 450).rotation(0.degrees)

                                onClick {

                                    if(allButton.find{it.button == this}?.isClicked == 0 )
                                    {
                                        println(this.text)
                                        UpdateContent(this.text)
                                        buttonList.add(this)
                                        this.colorMul=Colors.WHITE
                                        allButton.find{it.button == this}!!.isClicked  += 1
                                    }
                                    else if(allButton.find{it.button == this}?.isClicked  == 1 )
                                    {
                                        val index = buttonList.indexOf(this)
                                        contentInput.removeAt(index)
                                        InputText.text = "${contentInput.joinToString(separator = "")}"
                                        buttonList.remove(this)
                                        if(allButton.find{it.button == this}!!.isIce != 0)
                                        {
                                            this.colorMul = Colors.BLUE

                                        }
                                        else if(allButton.find{it.button == this}!!.transformIce != 0)
                                        {
                                            this.colorMul = Colors.DEEPSKYBLUE
                                        }
                                        else
                                        {
                                            this.colorMul = random1[random1[Colors.CYAN, Colors.WHITE], random1[Colors.YELLOW, Colors.CYAN]]
                                        }
                                        allButton.find{it.button == this}!!.isClicked  -= 1
                                    }

                                }

                            }.also {it.colorMul =  random1[random1[Colors.CYAN, Colors.WHITE], random1[Colors.YELLOW, Colors.CYAN]]
                            }
                            val a = newb.parent?.parent
                            a?.addChild(newb)
                            //screen_butons.add(newb) // gecikme yarattığı için yoruma alındı
                            allButton.add(MyObject(newb, 0, 0, 0,0))
                            newb.registerBodyWithFixture(type = BodyType.DYNAMIC, density = destiny, friction = 100.0,angularDamping = 50.0, gravityScale = 2.0)
                                .also { it.textColor = Colors.AZURE }
                                .also { it.textSize =cellSize/2 }
                                .also { it.textFont =font }





                            if (targetButton != null) {
                                if (targetButton.isIce != 0 ) {
                                    allButton.find { it.button == newb }!!.transformIce = 2
                                    allButton.find { it.button == newb }!!.button.colorMul = Colors.DEEPSKYBLUE

                                }
                            }

                        }
                    }
                }
                search_flag = 0
            }
        }.also { it.colorMul = Colors["#4effeb"] }
            .also { it.textColor = Colors.WHITE }
            .also { it.textSize =cellSize/1.5 }


        for (j in 0..2){
            for (i in 0..7) {

                //val  charRand = Random.nextInt(0,charrList.size)
                if(char_rate % 2 == 0)
                {
                    val  charRand = Random.nextInt(0,vovels_array.size)
                    button_text = vovels_array[charRand]
                }
                else
                {
                    val  charRand = Random.nextInt(0,consonants_array.size)
                    button_text = consonants_array[charRand]
                    if(char_rate == 5)
                    {
                        char_rate = 1
                    }
                }
                char_rate += 1

                val alp=  uiButton(width = boxSize, boxSize, text = button_text) {
                    position(13+ i * 63, 450).rotation(0.degrees)

                    onClick {

                        if(allButton.find{it.button == this}?.isClicked == 0 )
                        {
                            println(this.text)
                            UpdateContent(this.text)
                            buttonList.add(this)
                            this.colorMul=Colors.WHITE
                            allButton.find{it.button == this}!!.isClicked  += 1
                        }
                        else if(allButton.find{it.button == this}?.isClicked  == 1 )
                        {
                            val index = buttonList.indexOf(this)
                            contentInput.removeAt(index)
                            InputText.text = "${contentInput.joinToString(separator = "")}"
                            buttonList.remove(this)
                            if(allButton.find{it.button == this}!!.isIce != 0)
                            {
                                this.colorMul = Colors.BLUE

                            }
                            else if(allButton.find{it.button == this}!!.transformIce != 0)
                            {
                                this.colorMul = Colors.DEEPSKYBLUE
                            }
                            else
                            {
                                this.colorMul = random1[random1[Colors.CYAN, Colors.WHITE], random1[Colors.YELLOW, Colors.CYAN]]
                            }
                            allButton.find{it.button == this}!!.isClicked  -= 1
                        }

                    }

                }.also {it.colorMul =  random1[random1[Colors.CYAN, Colors.WHITE], random1[Colors.YELLOW, Colors.CYAN]]
                }.registerBodyWithFixture(type = BodyType.DYNAMIC, density = destiny, friction = 100.0,angularDamping = 50.0, gravityScale = 2.0)
                    .also { it.textColor = Colors.AZURE }
                    .also { it.textSize =cellSize/2 }
                    .also { it.textFont =font }

                //screen_butons.add(alp) // gecikme yarattığı için yoruma alındı
                allButton.add(MyObject(alp, 0, 0, 0,0))


            }}

        //süreye göre harf ekleme
        //GlobalScope.launch {



        while (true) {


            if (scane_switcher == 1) {

                //scores += "$score1"

                scane_switcher = 0
                sceneContainer.changeTo({ Scene2() })
                break
            }

            delay(delayTime - ((delayTime/10)*4))

            //val  charRand = Random.nextInt(0,charrList.size)

            if (char_rate % 2 == 0) {
                val charRand = Random.nextInt(0, vovels_array.size)
                button_text = vovels_array[charRand]
            } else {
                val charRand = Random.nextInt(0, consonants_array.size)
                button_text = consonants_array[charRand]
                if (char_rate == 5) {
                    char_rate = 1
                }
            }
            char_rate += 1

            val i = Random.nextInt(0, 8)
            val ice = Random.nextInt(0, 5)

            val alp = uiButton(width = boxSize, boxSize, text = button_text) {
                position(13 + i * 63, 450).rotation(0.degrees)

                onClick {

                    if (allButton.find { it.button == this }?.isClicked == 0) {
                        println(this.text)
                        UpdateContent(this.text)
                        buttonList.add(this)
                        this.colorMul = Colors.WHITE
                        allButton.find { it.button == this }!!.isClicked += 1
                    } else if (allButton.find { it.button == this }?.isClicked == 1) {
                        val index = buttonList.indexOf(this)
                        contentInput.removeAt(index)
                        InputText.text = "${contentInput.joinToString(separator = "")}"
                        buttonList.remove(this)
                        if (allButton.find { it.button == this }!!.isIce != 0) {
                            this.colorMul = Colors.BLUE

                        } else if (allButton.find { it.button == this }!!.transformIce != 0) {
                            this.colorMul = Colors.DEEPSKYBLUE
                        } else {
                            this.colorMul =
                                random1[random1[Colors.CYAN, Colors.WHITE], random1[Colors.YELLOW, Colors.CYAN]]
                        }
                        allButton.find { it.button == this }!!.isClicked -= 1
                    }


                }


            }.also {
                it.colorMul = random1[random1[Colors.CYAN, Colors.WHITE], random1[Colors.YELLOW, Colors.CYAN]]
            }.registerBodyWithFixture(
                type = BodyType.DYNAMIC,
                density = destiny,
                friction = 100.0,
                angularDamping = 50.0,
                gravityScale = 2.0
            )
                .also { it.textColor = Colors.AZURE }
                .also { it.textSize = cellSize / 2 }
                .also { it.textFont = font }





            /*
                        for (j in screen_butons.size - 1 downTo 0) {
                            val y = screen_butons[j].y
                            if (y < 600 && y.toInt() != 450) {
                                if (abs(13 + i * 63 - screen_butons[j].x) < 30) {
                                    //scane_switcher = 1
                                }
                            } else if (y > 650 && y.toInt() != 450) {
                                screen_butons.removeAt(j)
                            }
                        }*/


            val targetButton = allButton.filter { abs(13 + i * 63 - it.button.x) < 30 }
                .minByOrNull { it.button.y }


            if (ice == 1) {
                allButton.add(MyObject(alp, 0, 2, 0, 2))
                alp.colorMul = Colors.BLUE
            } else {
                allButton.add(MyObject(alp, 0, 0, 0, 0))
            }


            delay(delayTime - ((delayTime/10)*6))

            if ((targetButton != null) && (ice == 1) && targetButton.isIce == 0) {
                targetButton.transformIce = 2
                targetButton.button.colorMul = Colors.DEEPSKYBLUE
            } else if (targetButton != null) {
                if (targetButton.isIce != 0 && ice != 1) {
                    allButton.find { it.button == alp }!!.transformIce = 2
                    allButton.find { it.button == alp }!!.button.colorMul = Colors.DEEPSKYBLUE

                }
            }

            //screen_butons.add(alp)

            for (i in 0..7) {

                if(allButton.filter { abs(13 + i * 63 - it.button.x) < 30 }.size > 10)
                {
                    scane_switcher = 1
                }

            }


        }



        //}
    }


    fun UpdateContent(newChar: String) {
        contentInput.add(newChar)
        InputText.text = "${contentInput.joinToString(separator = "")}" // skor kutusunu güncelle
    }
    fun updateScore(newScore: Int) {
        score1 += newScore // skoru güncelle
        //süre azaltma
        if(score1>=400)
            delayTime=1000L
        else if(score1>=300)
            delayTime=2000L
        else if(score1>=200)
            delayTime=3000L
        else  if(score1>=100)
            delayTime=4000L

        scoreText.text = "$score1" // skor kutusunu güncelle
    }
    fun calculateWordPoints(word: String) {
        var points = 0
        for (char in word) {
            points += when (char.toUpperCase()) {
                'A' -> 1
                'B' -> 3
                'C' -> 4
                'Ç' -> 4
                'D' -> 3
                'E' -> 1
                'F' -> 7
                'G' -> 5
                'Ğ' -> 8
                'H' -> 5
                'I' -> 2
                'İ' -> 1
                'J' -> 10
                'K' -> 1
                'L' -> 1
                'M' -> 2
                'N' -> 1
                'O' -> 2
                'Ö' -> 7
                'P' -> 5
                'R' -> 1
                'S' -> 2
                'Ş' -> 4
                'T' -> 1
                'U' -> 2
                'Ü' -> 3
                'V' -> 7
                'Y' -> 3
                'Z' -> 4
                else -> 0
            }
        }
        updateScore(points)
    }

    fun turkishLowercase(s: String): String {
        val builder = StringBuilder()
        for (c in s) {
            when (c) {
                'I' -> builder.append('ı')
                'İ' -> builder.append('i')
                else -> builder.append(c.lowercase())
            }
        }
        return builder.toString()
    }


}

class MyObject(val button: UIButton, var isClicked: Int, var isIce: Int, var transformIce: Int, var value: Int)
