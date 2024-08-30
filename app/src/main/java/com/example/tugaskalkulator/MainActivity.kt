package com.example.tugaskalkulator

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.util.TypedValue
import com.example.tugaskalkulator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var inputDisplay = "0"
    private var resultDisplay = 0
    private var inputTextSize = 64f

    private fun inputValue(value: String){
        val operator = arrayOf("*", "/", "+", "-")
        if(inputDisplay == "0"){
            if(!operator.contains(value)){
                inputDisplay = value
            }
        } else {
            if(operator.contains(inputDisplay.last().toString()) && operator.contains(value)){
                inputDisplay = inputDisplay.substring(0, inputDisplay.length -1) + value
            } else {
                inputDisplay += value
            }
        }
        binding.txtDisplay.text = inputDisplay
    }

    private fun backspace(){
        if(inputDisplay.length > 1){
            inputDisplay = inputDisplay.substring(0, inputDisplay.length - 1)
        } else {
            if((inputDisplay.length == 1) && (inputDisplay != "0")){
                inputDisplay = "0"
            }
        }
        binding.txtDisplay.text = inputDisplay
    }

    private fun clearInput(){
        inputDisplay = "0"
        resultDisplay = 0
        binding.txtDisplay.text = inputDisplay
        binding.txtResult.text = resultDisplay.toString()
    }

    private fun calculate() {
        val operator = arrayOf("*", "/", "+", "-")
        if(operator.contains(inputDisplay.last().toString())){
            return
        }
        try {
            val numbers = Stack<Int>()
            val operators = Stack<Char>()

            var i = 0
            while (i < inputDisplay.length) {
                when {
                    inputDisplay[i].isDigit() -> {
                        var num = 0
                        while (i < inputDisplay.length && inputDisplay[i].isDigit()) {
                            num = num * 10 + (inputDisplay[i] - '0')
                            i++
                        }
                        numbers.push(num)
                        continue
                    }
                    inputDisplay[i] in "+-*/" -> {
                        while (operators.isNotEmpty() && precedence(operators.peek()) >= precedence(inputDisplay[i])) {
                            processOperation(numbers, operators)
                        }
                        operators.push(inputDisplay[i])
                    }
                }
                i++
            }

            while (operators.isNotEmpty()) {
                processOperation(numbers, operators)
            }

            resultDisplay = numbers.pop()
        } catch (e: Exception) {
            Toast.makeText(this, "Error in calculation", Toast.LENGTH_SHORT).show()
            resultDisplay = 0
        }

        inputDisplay = resultDisplay.toString()
        binding.txtDisplay.text = inputDisplay
        binding.txtResult.text = resultDisplay.toString()
    }

    private fun precedence(op: Char): Int {
        return when (op) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> 0
        }
    }

    private fun processOperation(numbers: Stack<Int>, operators: Stack<Char>) {
        val right = numbers.pop()
        val left = numbers.pop()
        val op = operators.pop()

        val result = when (op) {
            '+' -> left + right
            '-' -> left - right
            '*' -> left * right
            '/' -> left / right
            else -> 0
        }
        numbers.push(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            val buttons = mapOf(
                btn0 to "0",
                btn1 to "1",
                btn2 to "2",
                btn3 to "3",
                btn4 to "4",
                btn5 to "5",
                btn6 to "6",
                btn7 to "7",
                btn8 to "8",
                btn9 to "9",
                btnDivide to "/",
                btnMultiply to "*",
                btnAdd to "+",
                btnSubtract to "-"
            )
            txtDisplay.text = inputDisplay
            txtResult.text = resultDisplay.toString()

            buttons.forEach { button, value ->
                button.setOnClickListener {
                    inputValue(value)

                    if((inputDisplay.length > 10) && (inputTextSize >= 32f)){
                        inputTextSize -= 2f
                        txtDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
                    }
                }
            }
            delete.setOnClickListener {
                backspace()

                if((inputDisplay.length > 10) && (inputTextSize <= 64f)){
                    inputTextSize += 2f
                    txtDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
                }
            }
            btnClear.setOnClickListener {
                clearInput()
            }
            btnEquals.setOnClickListener {
                calculate()
                if((inputDisplay.length > 10) && (inputTextSize <= 64f)){
                    val newInputTextSize = (inputDisplay.length - 10) * 2f
                    inputTextSize = if((newInputTextSize >= 32f ) && (newInputTextSize <= 64f )) newInputTextSize else inputTextSize
                    txtDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
                } else {
                    inputTextSize = 64f
                    txtDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
                }
                Toast.makeText(this@MainActivity, resultDisplay.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
