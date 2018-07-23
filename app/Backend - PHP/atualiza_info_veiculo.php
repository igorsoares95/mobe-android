<?php

	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	$response = array("error" => FALSE);
	
	if(isset($_POST['marca']) && isset($_POST['modelo']) && isset($_POST['ano']) && isset($_POST['dispositivo']) && isset($_POST['placa'])) {
		
		$marca = $_POST['marca'];
		$modelo = $_POST['modelo'];
		$ano = $_POST['ano'];
		$dispositivo = $_POST['dispositivo'];
		$placa = $_POST['placa'];
				
		;
		
		if($bd->atualizaInfoVeiculo($marca, $modelo, $ano, $dispositivo, $placa)) {
		
			$response["error"] = FALSE;
			echo json_encode($response);
			
		} else {
			
			$response["error"] = TRUE;
			$response["error_msg"] = "Nenhuma informação foi alterada!";
			echo json_encode($response);
			
		}
		
		
	} else {
		
		$response["error"] = TRUE;
		$response["error_msg"] = "Preencha todos os campos";
		echo json_encode($response);
		
	}


?>