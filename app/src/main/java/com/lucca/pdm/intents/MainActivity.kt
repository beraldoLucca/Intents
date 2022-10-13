package com.lucca.pdm.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.net.Uri.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.giovani.pdm.intents.R
import com.lucca.pdm.intents.Constant.URL
import com.giovani.pdm.intents.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    //var pra devolver valor da intent (Activity Result Launcher)
    private lateinit var urlArl: ActivityResultLauncher<Intent>
    private lateinit var permissaoChamadaArl: ActivityResultLauncher<String>
    private lateinit var pegarImagemArl: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        supportActionBar?.subtitle = "MainActivity"


        urlArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ resultado ->
                if(resultado.resultCode == RESULT_OK ){
                    val urlRetomada = resultado.data?.getStringExtra(URL) ?: ""
                    amb.urlTv.text = urlRetomada
                }
        }

        //PEDIR PERMISSÃO AO USER PRA FAZER CHAMADA !!!
        permissaoChamadaArl = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            object: ActivityResultCallback<Boolean>{
                override fun onActivityResult(concedida: Boolean?) {
                    if(concedida !=null && concedida){
                        chamarNumero(true)      //user deu permissão, chama a função de fazer chamada telefonica
                    }
                    else{
                        Toast.makeText(this@MainActivity,
                            "Conceda permissão par a execução !",
                        Toast.LENGTH_SHORT).show()
                        finish()
                    } //onActivityResult ve se o user deu a permissão pra fazer a chamada
                }
            }
        )


        pegarImagemArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ resultado ->
            if(resultado.resultCode == RESULT_OK ){
                //receber path da imagem
                val imagemUri = resultado.data?.data
                imagemUri?.let {
                    amb.urlTv.text = it.toString()
                }

                //abrindo visualizador a partir do path da imagem
                val visualizarImagemIntent = Intent(ACTION_VIEW, imagemUri)
                startActivity(visualizarImagemIntent)

            }
        }


        //tratar click no botão a partir de seu ID do layout -> Fazer a INTENT
        amb.entrarUrlBt.setOnClickListener {
            //criar intent local
            val urlActivityIntent = Intent(this, UrlActivity::class.java)//UrlActivity é a classe q vai receber a intent(vamos tentar abri-la a partir da MainActivity)
            //val urlActivityIntent = Intent("SEGUNDA_TELA_DO_PROJETO_INTENTS") //outra forma de fazer a intent
            urlActivityIntent.putExtra(Constant.URL, amb.urlTv.text.toString()) //putExtra coloca o valor da TextView urlTv na intent;  URL é a chave associada ao valor de urlTv
            urlArl.launch(urlActivityIntent)    //lança(executa) a activity urlActivityIntent
        }

    }

    //colocar menu na action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflar o menu:
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //trata escolhas das opções do menu (faz elas fazerem algo) - pega pelo id do layout
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.viewMi -> {
                //abrir o navegador na url digitada pelo user
                val url = Uri.parse(amb.urlTv.text.toString())
                val navegadorIntent = Intent(ACTION_VIEW, url) //intent de uma action view da var url
                startActivity(navegadorIntent) //executa a activity navegadorIntent criada acima
                true
            }
            R.id.dialMi -> {
                chamarNumero(false)
                true
            }
            R.id.callMi ->{
                //verificar se a versão android é maior que marshmallow (23)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED){
                        //fazer a chamada se ja tem a permissão
                        chamarNumero(true)

                    }else{
                        //solicitar permissão
                        permissaoChamadaArl.launch(CALL_PHONE)
                    }
                }
                else{
                    chamarNumero(true)  //fazer a chamada para android menor que api 23 marshmallow
                }
                true
            }
            R.id.pickMi -> {
                //pick serve para exibir imagens
                //intent para pegar imagem
                val pegarImagemIntent = Intent(ACTION_PICK)
                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/*")
                startActivity(pegarImagemIntent)
                true
            }
            R.id.chooserMi -> {
                val escolherAppIntent = Intent(ACTION_CHOOSER)
                val informacoesIntent = Intent(ACTION_VIEW, Uri.parse(amb.urlTv.text.toString()))
                escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador")
                escolherAppIntent.putExtra(EXTRA_INTENT, informacoesIntent)

                startActivity(escolherAppIntent)
                true
            }

            else -> {false}
        }
    }

    private fun chamarNumero(chamar:Boolean){
        val uri = Uri.parse("tel: ${amb.urlTv.text}" )
        val intent = Intent(if (chamar) ACTION_CALL else ACTION_DIAL) //ACTION_CALL E DIAL são métodos prontos da classe Intent
        intent.data = uri
        startActivity(intent)
    }


}