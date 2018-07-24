<?php
	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	$response = array("error" => FALSE);
	
	if(isset($_POST['km']) && isset($_POST['id_dispositivo'])) {
	
		$km = $_POST['km'];
		$id_dispositivo = $_POST['id_dispositivo'];
		
		
		if($bd->verificaDispositivoEmUso($id_dispositivo)) {
			
			if($bd->somaKmDoVeiculo($km, $id_dispositivo)) {
				
				$response["error"] = FALSE;
				echo json_encode($response);
				return true;
			   
		    } else {
			   
			   $response["error"] = TRUE;;
				echo json_encode($response);
			   return false;
			   
		   }
						
		} else {
			
			$response["error"] = TRUE;;
			echo json_encode($response);
			return false;
		
		}
		
	}