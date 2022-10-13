package com.lucca.pdm.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lucca.pdm.intents.Constant.URL
import com.giovani.pdm.intents.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity() {

    private lateinit var aub:ActivityUrlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aub = ActivityUrlBinding.inflate(layoutInflater)
        setContentView(aub.root)
        supportActionBar?.subtitle = "UrlActivity"

        //receber da intent da MainActivity
        val urlAnterior = intent.getStringExtra(URL) ?: "" //procura uma String com a chave URL (mandamos na mainActivity) -> Se vier null, vira ""
        if (urlAnterior.isNotEmpty()){
            aub.urlEt.setText(urlAnterior)
        }

        //tratar clique no botão entrar URL da UrlActivity (listener) -> Devolver da UrlActivity pra MainActivity
        aub.entrarUrlBt.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val retornoIntent = Intent() //criar intent vazia
                retornoIntent.putExtra(URL, aub.urlEt.text.toString())  //atribuir valor do texto de urlEt e associar à var URL
                setResult(RESULT_OK, retornoIntent) //defini retornoIntent como resultado da intent ; 'RESULT_OK' significa q o user finalizou certo, clicando no botão
                finish() //finaliza a activity
            }
        })

    }
}

//urlEt é o id do EditText do layout da UrlActivity