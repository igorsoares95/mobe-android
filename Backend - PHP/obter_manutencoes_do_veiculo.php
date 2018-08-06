<?php
	
	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	//cria um array JSON
	$response = array("error" => FALSE);
	
	if(isset($_POST['id_veiculo_usuario'])){
		
		//recebe senha e email via POST
		$id_veiculo_usuario = $_POST['id_veiculo_usuario'];
		
		if($bd->obtemManutencoesDoVeiculo($id_veiculo_usuario)) {
			
			$response["manutencoes"] = $bd->obtemManutencoesDoVeiculo($id_veiculo_usuario);
			$response["error"] = FALSE;			
			echo json_encode($response);
			
		} else {
			
			$response["error"] = TRUE;
			$response["error_msg"] = "Não foi encontrado manutencoes para esse veiculo";
			echo json_encode($response);
			
		}
			
	}

?>