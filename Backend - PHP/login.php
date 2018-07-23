<?php
	
	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	//cria um array JSON
	$response = array("error" => FALSE);
	
	if( isset($_POST['email']) && isset($_POST['senha'])){
		
		//recebe senha e email via POST
		$email = $_POST['email'];
		$senha = $_POST['senha'];
		$usuario = $bd->obtemUsuarioPorEmailESenha($email, $senha);
		
		if($usuario){
			
			if($usuario['B_CONFIRMACAO']) {
				
				if(!$bd->verificaUsuarioInativo($email)) {
				
					$response["error"] = FALSE;
					$response["usuario"]["id"] = $usuario["ID"];
					$response["usuario"]["nome"] = $usuario["S_NOME"];
					$response["usuario"]["email"] = $usuario["S_EMAIL"];
					$response["usuario"]["telefone"] = $usuario["N_TELEFONE"];			
					echo json_encode($response);
				} else {
					
					$response["error"] = TRUE;			
					$response["usuario"]["email"] = $usuario["S_EMAIL"];
					$response["usuario"]["inativo"] = $usuario["B_INATIVO"];
					$response["error_msg"] = "Usuario inativo";
					echo json_encode($response);					
										
				}
				
			} else {
				
				if($bd->enviaEmailConfirmacao($email, $senha)){
					
					$response["error"] = TRUE;
					$response["usuario"]["inativo"] = $usuario["B_INATIVO"];
					$response["error_msg"] = "Usuário não confirmado, um link de confirmação foi enviado para seu email";
					echo json_encode($response);
					
				} else {
					
					$response["error"] = TRUE;
					$response["usuario"]["inativo"] = $usuario["B_INATIVO"];
					$response["error_msg"] = "Houve uma falha no Login, tente novamente.";
					echo json_encode($response);
					
				}
				
			}
			
		} else {
			
			//usuário não encontrado
			$response["error"] = TRUE;
			$response["error_msg"] = "Dados incorretos, tente novamente";
			echo json_encode($response);
						
		}
		
	} else {
		//parametros não preenchidos
		$response["error"] = TRUE;
		$response["error_msg"] = "Preencha todos os campos!";
		echo json_encode($response);
	}

?>