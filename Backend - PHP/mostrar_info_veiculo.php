<?php

	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	$response = array("error" => FALSE);
	
	if(isset($_POST['placa']) && isset($_POST['usuario'])) {
		
		$placa = $_POST['placa'];
		$usuario = $_POST['usuario'];
				
		$veiculo = $bd->mostraInfoVeiculo($placa, $usuario);
		
		if($veiculo) {
		
			$response["error"] = FALSE;
			$response["veiculo"]["marca"] = $veiculo["S_MARCA"];
			$response["veiculo"]["modelo"] = $veiculo["S_MODELO"];
			$response["veiculo"]["ano"] = $veiculo["D_ANO"];
			$response["veiculo"]["placa"] = $veiculo["S_PLACA"];
			$response["veiculo"]["km"] = $veiculo["N_KM"];
			$response["veiculo"]["dispositivo"] = $veiculo["N_DISPOSITIVO"];
			echo json_encode($response);
			
		} else {
			
			$response["error"] = TRUE;
			$response["error_msg"] = "Erro ao obter informações do veículo";
			echo json_encode($response);
			
		}
		
		
	} else {
		
		$response["error"] = TRUE;
		$response["error_msg"] = "Preencha todos os campos";
		echo json_encode($response);
		
	}


?>