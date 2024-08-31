package com.larissacardoso.aulafirebase


import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionSession.ActivityId
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.larissacardoso.aulafirebase.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    //instancia do firebase
    private val autenticacao by lazy{
        FirebaseAuth.getInstance()
    }

    //instancia para conectar com o banco de dados do firebase
    private val bancoDados by lazy{
        FirebaseFirestore.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView( binding.root)

        binding.btnExecutar.setOnClickListener{

            //metodo
            //salvarDados()
            //atualizarRemoverdados()
            listarDados()

            //cadastroUsuario()
            //logarUsuario()
          
        }

    }

    private fun salvarDadosUsuario(
        nome: String, idade: String)
    {
        val idUsuarioLogado = autenticacao.currentUser?.uid
        if(idUsuarioLogado != null){

            val dados = mapOf(
                "nome" to nome,
                "idade" to idade
            )

            bancoDados
                .collection("usuarios")
                .document(idUsuarioLogado)
                .set(dados)
                .addOnSuccessListener {  }
                .addOnFailureListener {  }

        }
    }


    private fun listarDados() {

        //salvarDadosUsuario("pedro", "24")
        val idUsuarioLogado = autenticacao.currentUser?.uid

        if( idUsuarioLogado != null ){
            val referenciaUsuario = bancoDados
                .collection("usuarios")
                .document(idUsuarioLogado)

            referenciaUsuario
                .get() //recupera os dados do banco
                .addOnSuccessListener { documentSnapshot ->
                    val dados = documentSnapshot.data
                    if( dados != null ) {
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        val texto = "Nome: $nome - Idade: $idade"

                        binding.txtResultado.text = texto
                    }

                }
                .addOnFailureListener {  }
        }

    }

    //função para atualizar e remover dados
    private fun atualizarRemoverdados() {

        val dados = mapOf(
            "nome" to "pedro",
            "idade" to "25"
            //"cpf" to "483..."
        )

        val idUsuarioLogado = autenticacao.currentUser?.uid

        val referenciaUsuario = bancoDados
            .collection("usuarios")
            //.document("1")
        //referenciaPedro.set(dados)

        referenciaUsuario
            //.update("nome", "pedro henrique ")
            //.delete()
            .add( dados )
            //para mostrar os dados atualizados
            .addOnSuccessListener {
                exibirMensagem("Usuário atualizado com sucesso!" )
            }.addOnFailureListener{
                    excepition -> exibirMensagem("Erro ao atualizae usuário: ${excepition.message}")
            }
        //referenciaPedro.delete()

    }

    //função para salvar dados
    private fun salvarDados() {

        val dados = mapOf(
            "nome" to "pedro",
            "idade" to "23",
            "cpf" to "483..."
        )

        bancoDados
            .collection("usuarios")
            .document("2")
            .set(dados)
            .addOnSuccessListener {
                exibirMensagem("Usuário salvo com sucesso!" )
            }.addOnFailureListener{
                excepition -> exibirMensagem("Erro ao salvar usuário: ${excepition.message}")
            }


    }

    //verificação de usuario logado
    override fun onStart() {
        super.onStart()
        //verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado(){

        //autenticacao.signOut()
        val usuario = autenticacao.currentUser
        val id = usuario?.uid

        if(usuario != null){
            exibirMensagem("Usuário está logado com id: $id")
            startActivity(
                Intent(this, PrincipalActivity::class.java)
            )
        }else{
            exibirMensagem("Não tem usuario logado")
        }
    }



    private fun logarUsuario() {
        //Dados adicionados pelo usuario
        val email = "pedro.henrique@gmail.com"
        val senha = "*1254Na@@"

        //Estivesse em uma tela de login
        autenticacao.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener { authResult ->
            binding.txtResultado.text = "Sucesso ao logar usuário!"
            startActivity(
                Intent(this, PrincipalActivity::class.java)
            )
        }.addOnFailureListener{ excepition ->
        binding.txtResultado.text = "Falha ao logar usuário! ${excepition.message}"}
    }

    private fun cadastroUsuario() {

        //Dados adicionados pelo usuario
        val email = "pedro.henrique@gmail.com"
        val senha = "*1254Na@@"
        val nome = "Pedro Henrique"
        val idade = "24"

        //Tela de cadastro do seu App
        autenticacao.createUserWithEmailAndPassword(
            email, senha

        ).addOnSuccessListener { authResult ->
            val email = authResult.user?.email
            val id = authResult.user?.uid
            //salvar mais dados do usuario no banco de dados
            salvarDadosUsuario(nome, idade)


            //exibirMensagem("Cadastro efetuado com sucesso: $id - $email")
            binding.txtResultado.text = "sucesso: $id - $email"

        }.addOnFailureListener{ exception ->
            val mensagemErro = exception.message
            binding.txtResultado.text = "Erro: $mensagemErro"
        }


    }

    private fun exibirMensagem(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()

    }
}