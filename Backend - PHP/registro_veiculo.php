<?php

	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	$response = array("error" => FALSE);
	
	if(isset($_POST['marca']) && isset($_POST['modelo']) && isset($_POST['ano']) && isset($_POST['placa']) && isset($_POST['km']) && isset($_POST['usuario']) && isset($_POST['dispositivo'])){
		
		$marca = $_POST['marca'];
		$modelo = $_POST['modelo'];
		$ano = $_POST['ano'];
		$placa = $_POST['placa'];
		$usuario = $_POST['usuario'];
		$dispositivo = $_POST['dispositivo'];
		$km = $_POST['km'];
		
		
		//checa se o veiculo existe		
		if($bd->verificaVeiculoExistente($placa)) {
			
			$response["error"] = TRUE;
			$response["error_msg"] = "Veiculo já existente ";
			echo json_encode($response);
			
		} else {
			
			if($bd->verificaUsuarioExistentePeloID($usuario)){
				
				if(!$bd->verificaDispositivoEmUso($dispositivo)) {
									
					if($bd->armazenaVeiculo($marca, $modelo, $ano, $placa, $km, $usuario, $dispositivo)) {
				
						$response["error"] = FALSE;
						echo json_encode($response);
				
					} else {
						
						$response["error"] = TRUE;
						$response["error_msg"] = "Erro durante o cadastro";
						echo json_encode($response);
						
					}
										
				} else {
					
					$response["error"] = TRUE;
					$response["error_msg"] = "Dispositivo em uso";
					echo json_encode($response);					
					
				}
				
			} else {
				
				$response["error"] = TRUE;
				$response["error_msg"] = "Usuário não existente";
				echo json_encode($response);								
			}
		}
			
	} else {
		
		$response["error"] = TRUE;
		$response["error_msg"] = "Preencha todos os campos";
		echo json_encode($response);
		
	}

?>