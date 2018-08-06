<?php
	
	require_once 'include/Funcoes_BD.php';
	$bd = new Funcoes_BD();
	
	//cria um array JSON
	$response = array("error" => FALSE);
	
	if(isset($_POST['id_modelo_veiculo'])){
		
		//recebe senha e email via POST
		$id_modelo_veiculo = $_POST['id_modelo_veiculo'];
		
		if($bd->obtemManutencoesRecomendadas($id_modelo_veiculo)) {
			
			$response["manutencoes"] = $bd->obtemManutencoesRecomendadas($id_modelo_veiculo);
			$response["error"] = FALSE;			
			echo json_encode($response);
			
		} else {
			
			$response["error"] = TRUE;
			$response["error_msg"] = "Não foi encontrada manutencoes recomendadas para esse modelo";
			echo json_encode($response);
			
		}
			
	}

?>