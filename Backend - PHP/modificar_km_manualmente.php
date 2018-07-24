<?php

	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	$response = array("error" => FALSE);
	
	if(isset($_POST['placa']) && isset($_POST['km'])) {
		
		$placa = $_POST['placa'];
		$km = $_POST['km'];
		
		if($bd->verificaVeiculoExistente($placa)) {
			
			if($bd->modificaKmManualmente($placa, $km)) {
			
				$response["error"] = FALSE;
				echo json_encode($response);
				
			} else {
				
				$response["error"] = TRUE;
				$response["error_msg"] = "Não foi possível modificar a km desse veículo";
				echo json_encode($response);
				
			}
				
		} else {
			
			$response["error"] = TRUE;
			$response["error_msg"] = "Veículo não existente!";
			echo json_encode($response);
			
		}
	} else {
		
		$response["error"] = TRUE;
		$response["error_msg"] = "Preencha todos os campos";
		echo json_encode($response);
		
	}


?>