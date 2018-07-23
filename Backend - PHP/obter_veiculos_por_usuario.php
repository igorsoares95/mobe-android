<?php
	
	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	//cria um array JSON
	$response = array("error" => FALSE);
	
	if(isset($_POST['idusuario'])){
		
		//recebe senha e email via POST
		$idusuario = $_POST['idusuario'];
		
		if($bd->obtemVeiculosPorUsuario($idusuario)) {
			
			$response["veiculos"] = $bd->obtemVeiculosPorUsuario($idusuario);
			$response["error"] = FALSE;			
			echo json_encode($response);
			
		} else {
			
			$response["error"] = TRUE;
			$response["error_msg"] = "Não foi encontrado veículos para esse usuário";
			echo json_encode($response);
			
		}
			
	}

?>