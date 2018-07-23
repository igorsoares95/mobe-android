<?php
	
	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	//cria um array JSON
	$response = array();
	
	if(isset($_POST['marca'])){
		
		//recebe senha e email via POST
		$marca = $_POST['marca'];
		
		
		if($bd->obtemModelosPorMarcaParaSpinner($marca)) {
			$response["veiculos"] = $bd->obtemModelosPorMarcaParaSpinner($marca);		
			echo json_encode($response);
			
		} else {
			
			$response = null;
			echo json_encode($response);
		}

			
			
			
			
	}

?>