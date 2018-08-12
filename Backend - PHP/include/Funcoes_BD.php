<?php

class Funcoes_BD{
	
	private $conn;
	
	function __construct(){
		require_once 'Conexao_BD.php';
		require_once 'Email.php';

			
		$bd = new Conexao_BD();
		$this->conn = $bd->conexao();
				
	}
	
	/* 	Armazena Usuário 
		Retorna detalhes do usuário
	*/
	
	public function armazenaUsuario($nome, $email, $telefone, $senha){
		
		$senha_criptografada = $this->criptoSenha($senha);
		$token = md5(time());
		$link = "http://projetocarro.zapto.org:888/manutencao_veicular/confirmacao.php?email=$email&token=$token";
		
		if(smtpmailer($email, 'guilhermerodrigues73@gmail.com', 'Projeto Carro', 'Confirmacao Email', 'Clique no link para confirmar sua conta: ' .$link)){
			
			$stmt = $this->conn->prepare("INSERT INTO tb_usuario(S_NOME, S_EMAIL, N_TELEFONE, S_SENHA, D_DATA_CRIACAO, S_TOKEN) VALUES( ?, ?, ?, ?, NOW(), ?)");
			$stmt->bind_param("sssss", $nome, $email, $telefone, $senha_criptografada, $token);
			$stmt->execute();
			$stmt->close();
			return true;			
			
		} else {
			
			return false;
			
		}
		
	}
	
	public function armazenaVeiculo($ano, $placa, $km, $id_usuario, $codigo_dispositivo, $id_modelo_veiculo) {
					
			$id_dispositivo = $this->obtemIDDispositivoPeloCodigo($codigo_dispositivo);

			$stmt = $this->conn->prepare("INSERT INTO tb_veiculo_do_usuario(D_ANO, S_PLACA, N_KM, ID_USUARIO, ID_DISPOSITIVO, ID_MODELO_VEICULO) VALUES( ?, ?, ?, ?, ?, ?)");
			$stmt->bind_param("ssssss", $ano, $placa, $km, $id_usuario, $id_dispositivo, $id_modelo_veiculo);
			
			if($stmt->execute()) {
				$stmt->close();
				return true;				
			} else {
				$stmt->close();
				return false;
			}		
	}
	
	public function criaManutencaoRecomendadaDoVeiculo($placa_veiculo_usuario, $id_manutencao_padrao, 
											  $km_antecipacao, $tempo_antecipacao, $data_ultima_manutencao, $km_ultima_manutencao) {
		
		$id_veiculo_usuario = $this->obtemIDVeiculoDoUsuarioPelaPlaca($placa_veiculo_usuario);
		
		$stmt = $this->conn->prepare("SELECT S_NOME, N_LIMITE_KM, N_LIMITE_TEMPO_MESES from tb_manutencao_padrao WHERE ID = ?");
		$stmt->bind_param("i", $id_manutencao_padrao);
		$stmt->execute();
		$manutencao_padrao = $stmt->get_result()->fetch_assoc();
		$stmt->close();
		$descricao = $manutencao_padrao['S_NOME'];
		$limite_km = $manutencao_padrao['N_LIMITE_KM'];
		$limite_tempo_meses = $manutencao_padrao['N_LIMITE_TEMPO_MESES'];
		
		
		$stmt = $this->conn->prepare("INSERT INTO tb_manutencao_do_veiculo(ID_VEICULO_DO_USUARIO, ID_MANUTENCAO_PADRAO, S_DESCRICAO, N_LIMITE_KM, N_LIMITE_TEMPO_MESES,
									 N_KM_ANTECIPACAO, N_TEMPO_ANTECIPACAO_MESES, D_DATA_ULTIMA_MANUTENCAO, N_KM_ULTIMA_MANUTENCAO) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bind_param("sisiiiiss",$id_veiculo_usuario, $id_manutencao_padrao, $descricao, $limite_km, $limite_tempo_meses,
						  $km_antecipacao, $tempo_antecipacao, $data_ultima_manutencao, $km_ultima_manutencao);
		
		if($stmt->execute()) {
			$stmt->close();
			return true;
		} else {
			$stmt->close();
			return false;
		}
		
	}
	
		public function criaManutencaoPersonalizadaDoVeiculo($placa_veiculo_usuario, $descricao, $limite_km, $limite_tempo_meses,
											  $km_antecipacao, $tempo_antecipacao, $data_ultima_manutencao, $km_ultima_manutencao) {
		
		$id_veiculo_usuario = $this->obtemIDVeiculoDoUsuarioPelaPlaca($placa_veiculo_usuario);
			
		$stmt = $this->conn->prepare("INSERT INTO tb_manutencao_do_veiculo(ID_VEICULO_DO_USUARIO, ID_MANUTENCAO_PADRAO, S_DESCRICAO, N_LIMITE_KM, N_LIMITE_TEMPO_MESES,
									 N_KM_ANTECIPACAO, N_TEMPO_ANTECIPACAO_MESES, D_DATA_ULTIMA_MANUTENCAO, N_KM_ULTIMA_MANUTENCAO) VALUES(?, NULL, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bind_param("ssiiiiss",$id_veiculo_usuario, $descricao, $limite_km, $limite_tempo_meses,
						  $km_antecipacao, $tempo_antecipacao, $data_ultima_manutencao, $km_ultima_manutencao);
		
		if($stmt->execute()) {
			$stmt->close();
			return true;
		} else {
			$stmt->close();
			return false;
		}
		
	}
	
	public function obtemIDDispositivoPeloCodigo($codigo_dispositivo) {
		
		$stmt = $this->conn->prepare("SELECT ID FROM tb_dispositivo WHERE S_CODIGO = ?");
		$stmt->bind_param("s",$codigo_dispositivo);
		$stmt->execute();
		
		$dispositivo = $stmt->get_result()->fetch_assoc();
		$stmt->close();
		
		return $dispositivo['ID'];
				
	}
		
	
	/* Verifica e retorna Usuário atraves do email e senha*/
	public function obtemUsuarioPorEmailESenha($email, $senha){
		
		$stmt = $this->conn->prepare("SELECT * FROM tb_usuario WHERE S_EMAIL = ?");
		$stmt->bind_param("s",$email);
		
		if($stmt->execute()){
			
			$usuario = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			
			if($this->criptoSenha($senha) == $usuario['S_SENHA']){
				
				return $usuario;
				
			} else {
			
				return NULL;
			
			}
				
		} else {
			
			return NULL;
		}
		
	}
	
	public function verificaUsuarioExistente($email){
		
		$stmt = $this->conn->prepare("SELECT S_EMAIL from tb_usuario WHERE S_EMAIL = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
		
		if ($stmt->num_rows > 0) {
            // usuário existe
            $stmt->close();
            return true;
        } else {
            // usuário não existe
            $stmt->close();
            return false;
        }
	}
	
	public function verificaUsuarioExistentePeloID($id){
		
		$stmt = $this->conn->prepare("SELECT S_EMAIL from tb_usuario WHERE ID = ?");
        $stmt->bind_param("s", $id);
        $stmt->execute();
        $stmt->store_result();
		
		if ($stmt->num_rows > 0) {
            // usuário existe
            $stmt->close();
            return true;
        } else {
            // usuário não existe
            $stmt->close();
            return false;
        }
	}
	
	public function verificaDispositivoExistente($codigo_dispositivo){
		
		$stmt = $this->conn->prepare("SELECT S_CODIGO from tb_dispositivo WHERE S_CODIGO = ?");
        $stmt->bind_param("s", $codigo_dispositivo);
        $stmt->execute();
        $stmt->store_result();
		
		if ($stmt->num_rows > 0) {
            $stmt->close();
            return true;
        } else {
            $stmt->close();
            return false;
        }
	}
	
	public function verificaDispositivoAtivo($codigo_dispositivo){
		
		$stmt = $this->conn->prepare("SELECT S_CODIGO from tb_dispositivo WHERE S_CODIGO = ? AND B_ATIVO = 1");
        $stmt->bind_param("s", $codigo_dispositivo);
        $stmt->execute();
        $stmt->store_result();
		
		if ($stmt->num_rows > 0) {
            $stmt->close();
            return true;
        } else {
            $stmt->close();
            return false;
        }
	}
	
	public function verificaDispositivoEmUso($codigo_dispositivo) {
		
		
		$stmt = $this->conn->prepare("SELECT b.S_CODIGO FROM tb_veiculo_do_usuario AS a INNER JOIN tb_dispositivo AS b ON a.ID_DISPOSITIVO = b.ID WHERE b.S_CODIGO = ?");
		$stmt->bind_param("s",$codigo_dispositivo);
		$stmt->execute();
		$stmt->store_result();
		
		if ($stmt->num_rows > 0) {
            $stmt->close();
            return true;
        } else {
            $stmt->close();
            return false;
        }      
	
	}
	
			
	public function verificaVeiculoExistente($placa) {
		
		$stmt = $this->conn->prepare("SELECT S_PLACA from tb_veiculo_do_usuario WHERE S_PLACA = ?");
        $stmt->bind_param("s", $placa);
        $stmt->execute();
        $stmt->store_result();
		
		if ($stmt->num_rows > 0) {
            $stmt->close();
            return true;
        } else {
            $stmt->close();
            return false;
        }
	}
	
	
	
	public function enviaEmailConfirmacao($email, $senha){
		
		$usuario = $this->obtemUsuarioPorEmailESenha($email, $senha);
		
		if($usuario) {
			
			$token = $usuario['S_TOKEN'];
			$link = "http://projetocarro.zapto.org:888/manutencao_veicular/confirmacao.php?email=$email&token=$token";
			if(smtpmailer($email, 'guilhermerodrigues73@gmail', 'Projeto Carro', 'Confirmacao Email', 'Clique no link para confirmar seu email: ' .$link)){
				
				return true;
				
			} else {
				
				return false;
				
			}
		} else {
			
			return false;
			
		}
	}
	
	public function verificaUsuarioPorEmailEToken($email, $token){
		
		$stmt = $this->conn->prepare("SELECT * FROM tb_usuario WHERE S_EMAIL = ? AND S_TOKEN = ?");
		$stmt->bind_param("ss", $email, $token);
		$stmt->execute();
		$stmt->store_result();
		
		if($stmt->num_rows>0){
			
			$stmt->close();
			return true;
			
		} else {
			
			$stmt->close();
			return false;
			
		}
		
	}
	
	public function verificaUsuarioConfirmado($email, $token){
		
		$stmt = $this->conn->prepare("SELECT * FROM tb_usuario WHERE S_EMAIL = ? AND S_TOKEN = ? AND B_CONFIRMACAO = 1");
		$stmt->bind_param("ss", $email, $token);
		$stmt->execute();
		$stmt->store_result();
		
		if($stmt->num_rows>0){
			
			$stmt->close();
			return true;
			
		} else {
			
			$stmt->close();
			return false;
			
		}
		
	}
	
	public function confirmaUsuario($email, $token) {
	
		$stmt = $this->conn->prepare("UPDATE tb_usuario SET B_CONFIRMACAO = 1 WHERE S_EMAIL = ? AND S_TOKEN = ?");
		$stmt->bind_param("ss",$email, $token);
		
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        else {
			$stmt->close();
            return false;
        } 
	
	}
	
	public function atualizaUsuario($nome, $email, $telefone){
		
		$stmt = $this->conn->prepare("SELECT S_NOME, N_TELEFONE from tb_usuario WHERE S_EMAIL = ?");
		$stmt->bind_param("s",$email);
		$stmt->execute();
		$result = $stmt->get_result()->fetch_assoc();
		$stmt->close();
		
		$nome_antigo = $result["S_NOME"];
		$telefone_antigo = $result["N_TELEFONE"];
		
		if($nome == $nome_antigo && $telefone == $telefone_antigo) {
			
			return false;
		
		} else {
			
			$stmt = $this->conn->prepare("UPDATE tb_usuario SET S_NOME = ?, N_TELEFONE = ? WHERE S_EMAIL = ?");
			$stmt->bind_param("sss", $nome, $telefone, $email);
			$result = $stmt->execute();
			$stmt->close();
			
			if($result) {
				
				$stmt = $this->conn->prepare("SELECT * FROM tb_usuario WHERE S_EMAIL = ?");
				$stmt->bind_param("s", $email);
				$stmt->execute();
				$usuario = $stmt->get_result()->fetch_assoc();
				$stmt->close();
				
				return $usuario;
				
			} else {
				
				return false;
				
			}
		}		
	}
	
	public function atualizaInfoManutencaoDoVeiculo($id_manutencao_do_usuario, $limite_km, $limite_tempo_meses, $km_antecipacao, $tempo_antecipacao_meses,
													$data_ultima_manutencao, $km_ultima_manutencao){
		
		$stmt = $this->conn->prepare("SELECT N_LIMITE_KM, N_LIMITE_TEMPO_MESES, N_KM_ANTECIPACAO, N_TEMPO_ANTECIPACAO_MESES, D_DATA_ULTIMA_MANUTENCAO, N_KM_ULTIMA_MANUTENCAO
									 FROM tb_manutencao_do_veiculo WHERE ID = ?");
		$stmt->bind_param("i", $id_manutencao_do_usuario);
		$stmt->execute();
		$result = $stmt->get_result()->fetch_assoc();
		$stmt->close();
		
		$limite_km_antigo = $result["N_LIMITE_KM"];
		$limite_tempo_meses_antigo = $result["N_LIMITE_TEMPO_MESES"];
		$km_antecipacao_antigo = $result["N_KM_ANTECIPACAO"];
		$tempo_antecipacao_meses_antigo = $result["N_TEMPO_ANTECIPACAO_MESES"];
		$data_ultima_manutencao_antigo = $result["D_DATA_ULTIMA_MANUTENCAO"];
		$km_ultima_manutencao_antigo = $result["N_KM_ULTIMA_MANUTENCAO"];
		
		if($limite_km == $limite_km_antigo && $limite_tempo_meses == $limite_tempo_meses_antigo && $km_antecipacao == $km_antecipacao_antigo
		   && $tempo_antecipacao_meses == $tempo_antecipacao_meses_antigo && $data_ultima_manutencao == $data_ultima_manutencao_antigo
		   && $km_ultima_manutencao == $km_ultima_manutencao_antigo) {
			
			return false;
		
		} else {
			
			$stmt = $this->conn->prepare("UPDATE tb_manutencao_do_veiculo SET N_LIMITE_KM = ?, N_LIMITE_TEMPO_MESES = ?, N_KM_ANTECIPACAO = ?, N_TEMPO_ANTECIPACAO_MESES = ?, D_DATA_ULTIMA_MANUTENCAO = ?, N_KM_ULTIMA_MANUTENCAO = ?
										 WHERE ID = ?");
			$stmt->bind_param("iiiisii", $limite_km, $limite_tempo_meses, $km_antecipacao, $tempo_antecipacao_meses, $data_ultima_manutencao, $km_ultima_manutencao, $id_manutencao_do_usuario);
			$result = $stmt->execute();
			$stmt->close();
			return true;
		}		
	}

	
	public function obtemManutencoesRecomendadas($id_modelo_veiculo) {
		
		$stmt = $this->conn->prepare("SELECT ID, S_NOME, N_LIMITE_KM, N_LIMITE_TEMPO_MESES FROM tb_manutencao_padrao WHERE ID_MODELO_VEICULO = ?");
		$stmt->bind_param("i", $id_modelo_veiculo);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				$manutencoes = array();				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao = array();
					$manutencao["id_manutencao_padrao"] = $linha["ID"];
					$manutencao["nome"] = $linha["S_NOME"];
					$manutencao["limite_km"] = $linha["N_LIMITE_KM"];
					$manutencao["limite_tempo_meses"] = $linha["N_LIMITE_TEMPO_MESES"];
					array_push($manutencoes,$manutencao);
				}
				$stmt->close();				
				return $manutencoes;
										
			} else {
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}		
	}
	
	public function realizaManutencao($data_ultima_manutencao, $km_ultima_manutencao, $id_manutencao_do_veiculo) {
		
		$stmt = $this->conn->prepare("UPDATE tb_manutencao_do_veiculo SET D_DATA_ULTIMA_MANUTENCAO = ?, N_KM_ULTIMA_MANUTENCAO = ? WHERE ID = ?");
		$stmt->bind_param("sis",$data_ultima_manutencao, $km_ultima_manutencao, $id_manutencao_do_veiculo);
		
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        else {
			$stmt->close();
            return false;
        } 
		
	}
	
	
	
	public function obtemVeiculosPorUsuario($id_usuario) {
		
		$stmt = $this->conn->prepare("SELECT a.ID, a.D_ANO, a.S_PLACA, a.N_KM, b.S_CODIGO, c.S_MODELO, d.S_MARCA FROM tb_veiculo_do_usuario AS a
									 INNER JOIN tb_dispositivo AS b ON a.ID_DISPOSITIVO = b.ID
									 INNER JOIN tb_modelo_de_veiculo AS c ON a.ID_MODELO_VEICULO = c.ID
									 INNER JOIN tb_marca_de_veiculo AS d ON c.ID_MARCA = d.ID WHERE a.ID_USUARIO = ?");
		$stmt->bind_param("s", $id_usuario);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				$veiculos = array();				
				while($linha = $result->fetch_assoc()) {
					
					$veiculo = array();
					$veiculo["id_veiculo_do_usuario"] = $linha["ID"];
					$veiculo["ano"] = $linha["D_ANO"];
					$veiculo["placa"] = $linha["S_PLACA"];
					$veiculo["km"] = $linha["N_KM"];
					$veiculo["codigo_dispositivo"] = $linha["S_CODIGO"];
					$veiculo["modelo"] = $linha["S_MODELO"];
					$veiculo["marca"] = $linha["S_MARCA"];
					array_push($veiculos,$veiculo);
				}
				$stmt->close();				
				return $veiculos;
										
			} else {
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}		
	}
	
	public function obtemManutencoesProximasDoUsuario($id_usuario) {
		
		$manutencoes_proximas = array();				
		
		$stmt = $this->conn->prepare("SELECT a.ID, c.S_MODELO, b.S_PLACA, b.N_KM, a.S_DESCRICAO, a.N_KM_ULTIMA_MANUTENCAO, a.N_LIMITE_KM, a.D_DATA_ULTIMA_MANUTENCAO, a.N_LIMITE_TEMPO_MESES FROM tb_manutencao_do_veiculo AS a
									 INNER JOIN tb_veiculo_do_usuario AS b ON a.ID_VEICULO_DO_USUARIO = b.ID
									 INNER JOIN tb_modelo_de_veiculo AS c ON b.ID_MODELO_VEICULO = c.ID
									 INNER JOIN tb_dispositivo AS d ON b.ID_DISPOSITIVO = d.ID
									 INNER JOIN tb_usuario AS e ON b.ID_USUARIO = e.ID
									 WHERE (e.ID = ?) AND (b.N_KM >= a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM - a.N_KM_ANTECIPACAO AND b.N_KM < a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM) OR (NOW() >= DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES-N_TEMPO_ANTECIPACAO_MESES month) AND NOW() < DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES month))");
		$stmt->bind_param("i",$id_usuario);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao_proxima = array();
					$manutencao_proxima["id_manutencao_do_veiculo"] = $linha["ID"];
					$manutencao_proxima["modelo_veiculo"] = $linha["S_MODELO"];
					$manutencao_proxima["placa"] = $linha["S_PLACA"];
					$manutencao_proxima["km_atual"] = $linha["N_KM"];
					$manutencao_proxima["descricao"] = $linha["S_DESCRICAO"];
					$manutencao_proxima["km_proxima_manutencao"] = $linha["N_LIMITE_KM"] + $linha["N_KM_ULTIMA_MANUTENCAO"];
					$tempo_limite_meses = $linha["N_LIMITE_TEMPO_MESES"];				
					$manutencao_proxima["data_proxima_manutencao"] = date('Y-m-d', strtotime("+$tempo_limite_meses month", strtotime($linha["D_DATA_ULTIMA_MANUTENCAO"])));					
					$manutencao_proxima["status"] = "proxima";
					array_push($manutencoes_proximas,$manutencao_proxima);
					
				}
							
				$stmt->close();
				return $manutencoes_proximas;
										
			} else {
				
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}		
		
	}
	
	
	public function obtemManutencoesAtrasadasDoUsuario($id_usuario) {
		
		$manutencoes_atrasadas = array();				
		
		$stmt = $this->conn->prepare("SELECT a.ID, c.S_MODELO, b.S_PLACA, b.N_KM, a.S_DESCRICAO, a.N_KM_ULTIMA_MANUTENCAO, a.N_LIMITE_KM, a.D_DATA_ULTIMA_MANUTENCAO, a.N_LIMITE_TEMPO_MESES
									FROM tb_manutencao_do_veiculo AS a
									INNER JOIN tb_veiculo_do_usuario AS b ON a.ID_VEICULO_DO_USUARIO = b.ID
                                    INNER JOIN tb_modelo_de_veiculo AS c ON b.ID_MODELO_VEICULO = c.ID
                                    INNER JOIN tb_dispositivo AS d ON b.ID_DISPOSITIVO = d.ID
									INNER JOIN tb_usuario AS e ON b.ID_USUARIO = e.ID
									WHERE (e.ID = ?) AND b.N_KM >= a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM OR NOW() >= DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES month)");
		$stmt->bind_param("s",$id_usuario);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao_atrasada = array();
					$manutencao_atrasada["id_manutencao_do_veiculo"] = $linha["ID"];
					$manutencao_atrasada["modelo_veiculo"] = $linha["S_MODELO"];
					$manutencao_atrasada["placa"] = $linha["S_PLACA"];
					$manutencao_atrasada["km_atual"] = $linha["N_KM"];
					$manutencao_atrasada["descricao"] = $linha["S_DESCRICAO"];
					$manutencao_atrasada["km_proxima_manutencao"] = $linha["N_LIMITE_KM"] + $linha["N_KM_ULTIMA_MANUTENCAO"];
					$tempo_limite_meses = $linha["N_LIMITE_TEMPO_MESES"];				
					$manutencao_atrasada["data_proxima_manutencao"] = date('Y-m-d', strtotime("+$tempo_limite_meses month", strtotime($linha["D_DATA_ULTIMA_MANUTENCAO"])));					
					$manutencao_atrasada["status"] = "proxima";
					array_push($manutencoes_atrasadas,$manutencao_atrasada);
					
				}
							
				$stmt->close();
				return $manutencoes_atrasadas;
										
			} else {
				
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}		
		
	}
	
	public function obtemManutencoesProximasDoVeiculoDoUsuario($id_veiculo_do_usuario) {
		
		$manutencoes_proximas = array();				
		
		$stmt = $this->conn->prepare("SELECT a.ID, c.S_MODELO, b.S_PLACA, b.N_KM, a.S_DESCRICAO, a.N_KM_ULTIMA_MANUTENCAO, a.N_LIMITE_KM, a.D_DATA_ULTIMA_MANUTENCAO, a.N_LIMITE_TEMPO_MESES FROM tb_manutencao_do_veiculo AS a
									 INNER JOIN tb_veiculo_do_usuario AS b ON a.ID_VEICULO_DO_USUARIO = b.ID
									 INNER JOIN tb_modelo_de_veiculo AS c ON b.ID_MODELO_VEICULO = c.ID
									 INNER JOIN tb_dispositivo AS d ON b.ID_DISPOSITIVO = d.ID
									 INNER JOIN tb_usuario AS e ON b.ID_USUARIO = e.ID
									 WHERE (b.ID = ?) AND (b.N_KM >= a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM - a.N_KM_ANTECIPACAO AND b.N_KM < a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM) OR (NOW() >= DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES-N_TEMPO_ANTECIPACAO_MESES month) AND NOW() < DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES month))");
		$stmt->bind_param("i",$id_veiculo_do_usuario);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao_proxima = array();
					$manutencao_proxima["id_manutencao_do_veiculo"] = $linha["ID"];
					$manutencao_proxima["modelo_veiculo"] = $linha["S_MODELO"];
					$manutencao_proxima["placa"] = $linha["S_PLACA"];
					$manutencao_proxima["km_atual"] = $linha["N_KM"];
					$manutencao_proxima["descricao"] = $linha["S_DESCRICAO"];
					$manutencao_proxima["km_proxima_manutencao"] = $linha["N_LIMITE_KM"] + $linha["N_KM_ULTIMA_MANUTENCAO"];
					$tempo_limite_meses = $linha["N_LIMITE_TEMPO_MESES"];				
					$manutencao_proxima["data_proxima_manutencao"] = date('Y-m-d', strtotime("+$tempo_limite_meses month", strtotime($linha["D_DATA_ULTIMA_MANUTENCAO"])));					
					$manutencao_proxima["status"] = "proxima";
					array_push($manutencoes_proximas,$manutencao_proxima);
					
				}
							
				$stmt->close();
				return $manutencoes_proximas;
										
			} else {
				
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}		
		
	}	
	
	
	public function obtemManutencoesAtrasadasDoVeiculoDoUsuario($id_veiculo_do_usuario) {
		
		$manutencoes_atrasadas = array();				
		
		$stmt = $this->conn->prepare("SELECT a.ID, c.S_MODELO, b.S_PLACA, b.N_KM, a.S_DESCRICAO, a.N_KM_ULTIMA_MANUTENCAO, a.N_LIMITE_KM, a.D_DATA_ULTIMA_MANUTENCAO, a.N_LIMITE_TEMPO_MESES
									FROM tb_manutencao_do_veiculo AS a
									INNER JOIN tb_veiculo_do_usuario AS b ON a.ID_VEICULO_DO_USUARIO = b.ID
                                    INNER JOIN tb_modelo_de_veiculo AS c ON b.ID_MODELO_VEICULO = c.ID
                                    INNER JOIN tb_dispositivo AS d ON b.ID_DISPOSITIVO = d.ID
									INNER JOIN tb_usuario AS e ON b.ID_USUARIO = e.ID
									WHERE (b.ID = ?) AND b.N_KM >= a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM OR NOW() >= DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES month)");
		$stmt->bind_param("s",$id_veiculo_do_usuario);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao_atrasada = array();
					$manutencao_atrasada["id_manutencao_do_veiculo"] = $linha["ID"];
					$manutencao_atrasada["modelo_veiculo"] = $linha["S_MODELO"];
					$manutencao_atrasada["placa"] = $linha["S_PLACA"];
					$manutencao_atrasada["km_atual"] = $linha["N_KM"];
					$manutencao_atrasada["descricao"] = $linha["S_DESCRICAO"];
					$manutencao_atrasada["km_proxima_manutencao"] = $linha["N_LIMITE_KM"] + $linha["N_KM_ULTIMA_MANUTENCAO"];
					$tempo_limite_meses = $linha["N_LIMITE_TEMPO_MESES"];				
					$manutencao_atrasada["data_proxima_manutencao"] = date('Y-m-d', strtotime("+$tempo_limite_meses month", strtotime($linha["D_DATA_ULTIMA_MANUTENCAO"])));					
					$manutencao_atrasada["status"] = "proxima";
					array_push($manutencoes_atrasadas,$manutencao_atrasada);
					
				}
							
				$stmt->close();
				return $manutencoes_atrasadas;
										
			} else {
				
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}		
		
	}
	
	
	
	
	public function notificaManutencoesAtrasadasEProximas($codigo_dispositivo) {
		
		//----------------manutencoes proximas------------------------------
		$manutencoes_atrasadas = array();
		$manutencoes_proximas = array();				
		
		$stmt = $this->conn->prepare("SELECT c.S_MODELO, b.S_PLACA, b.N_KM, a.S_DESCRICAO, a.N_KM_ULTIMA_MANUTENCAO, a.N_LIMITE_KM, a.D_DATA_ULTIMA_MANUTENCAO, a.N_LIMITE_TEMPO_MESES
									FROM tb_manutencao_do_veiculo AS a
									INNER JOIN tb_veiculo_do_usuario AS b ON a.ID_VEICULO_DO_USUARIO = b.ID
                                    INNER JOIN tb_modelo_de_veiculo AS c ON b.ID_MODELO_VEICULO = c.ID
                                    INNER JOIN tb_dispositivo AS d ON b.ID_DISPOSITIVO = d.ID
									WHERE d.S_CODIGO = ? AND b.N_KM >= a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM - a.N_KM_ANTECIPACAO AND b.N_KM < a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM
									OR NOW() >= DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES-N_TEMPO_ANTECIPACAO_MESES month) AND NOW() < DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES month)");
		$stmt->bind_param("s",$codigo_dispositivo);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao_proxima = array();
					$manutencao_proxima["modelo_veiculo"] = $linha["S_MODELO"];
					$manutencao_proxima["placa"] = $linha["S_PLACA"];
					$manutencao_proxima["km_atual"] = $linha["N_KM"];
					$manutencao_proxima["descricao"] = $linha["S_DESCRICAO"];
					$manutencao_proxima["km_ultima_manutencao"] = $linha["N_KM_ULTIMA_MANUTENCAO"];
					$manutencao_proxima["km_proxima_manutencao"] = $linha["N_LIMITE_KM"] + $linha["N_KM_ULTIMA_MANUTENCAO"];
					$manutencao_proxima["data_ultima_manutencao"] = $linha["D_DATA_ULTIMA_MANUTENCAO"];					
					$tempo_limite_meses = $linha["N_LIMITE_TEMPO_MESES"];				
					$manutencao_proxima["data_proxima_manutencao"] = date('Y-m-d', strtotime("+$tempo_limite_meses month", strtotime($linha["D_DATA_ULTIMA_MANUTENCAO"])));					
					$manutencao_proxima["status"] = "proxima";
					array_push($manutencoes_proximas,$manutencao_proxima);
					
				}
							
				$stmt->close();				
										
			} else {
				$stmt->close();
			}
			
		} else {
			$stmt->close();
		}
		
		//----------------manutencoes atrasadas------------------------------
		
		$stmt = $this->conn->prepare("SELECT c.S_MODELO, b.S_PLACA, b.N_KM, a.S_DESCRICAO, a.N_KM_ULTIMA_MANUTENCAO, a.N_LIMITE_KM, a.D_DATA_ULTIMA_MANUTENCAO, a.N_LIMITE_TEMPO_MESES
									FROM tb_manutencao_do_veiculo AS a
									INNER JOIN tb_veiculo_do_usuario AS b ON a.ID_VEICULO_DO_USUARIO = b.ID
                                    INNER JOIN tb_modelo_de_veiculo AS c ON b.ID_MODELO_VEICULO = c.ID
                                    INNER JOIN tb_dispositivo AS d ON b.ID_DISPOSITIVO = d.ID
									WHERE d.S_CODIGO = ? AND b.N_KM >= a.N_KM_ULTIMA_MANUTENCAO + a.N_LIMITE_KM OR NOW() >= DATE_ADD(a.D_DATA_ULTIMA_MANUTENCAO, interval N_LIMITE_TEMPO_MESES month)");
		$stmt->bind_param("s",$codigo_dispositivo);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao_atrasada = array();
					$manutencao_atrasada["modelo_veiculo"] = $linha["S_MODELO"];
					$manutencao_atrasada["placa"] = $linha["S_PLACA"];
					$manutencao_atrasada["km_atual"] = $linha["N_KM"];
					$manutencao_atrasada["descricao"] = $linha["S_DESCRICAO"];
					$manutencao_atrasada["km_ultima_manutencao"] = $linha["N_KM_ULTIMA_MANUTENCAO"];
					$manutencao_atrasada["km_proxima_manutencao"] = $linha["N_LIMITE_KM"] + $linha["N_KM_ULTIMA_MANUTENCAO"];
					$manutencao_atrasada["data_ultima_manutencao"] = $linha["D_DATA_ULTIMA_MANUTENCAO"];					
					$tempo_limite_meses = $linha["N_LIMITE_TEMPO_MESES"];				
					$manutencao_atrasada["data_proxima_manutencao"] = date('Y-m-d', strtotime("+$tempo_limite_meses month", strtotime($linha["D_DATA_ULTIMA_MANUTENCAO"])));				
					//acrescenta 1 mes em uma data
					//$linha["D_DATA_ULTIMA_MANUTENCAO"] = date('Y-m-d', strtotime("+1 month", strtotime($linha["D_DATA_ULTIMA_MANUTENCAO"])));					
					$manutencao_atrasada["status"] = "atrasada";
					array_push($manutencoes_atrasadas,$manutencao_atrasada);
				}
													
				$stmt->close();				
										
			} else {
				$stmt->close();
			}
			
		} else {
			$stmt->close();
		}
		
		$manutencoes = array();
		$manutencoes = array_merge($manutencoes_proximas, $manutencoes_atrasadas);		
			
		$qtd_manutencoes_proximas = count($manutencoes_proximas);
		$qtd_manutencoes_atrasadas = count($manutencoes_atrasadas);
		
		$stmt = $this->conn->prepare("SELECT c.S_FIREBASE_REG_ID FROM tb_manutencao_do_veiculo AS a
									 INNER JOIN tb_veiculo_do_usuario AS b ON a.ID_VEICULO_DO_USUARIO = b.ID
									 INNER JOIN tb_usuario AS c ON b.ID_USUARIO = c.ID
									 INNER JOIN tb_dispositivo AS d ON b.ID_DISPOSITIVO = d.ID
									 WHERE d.S_CODIGO = ?");
		$stmt->bind_param("s",$codigo_dispositivo);
		$stmt->execute();
		$result = $stmt->get_result()->fetch_assoc();
		$stmt->close();
		$reg_id = $result['S_FIREBASE_REG_ID'];
		
		
		if($qtd_manutencoes_atrasadas > 0 || $qtd_manutencoes_proximas > 0) {
			
			error_reporting(-1);
			ini_set('display_errors', 'On');
			
			require_once 'firebase/firebase.php';
			require_once 'firebase/push.php';
	
			$firebase = new Firebase();
			$push = new Push();
			
			// optional payload
			$payload = $manutencoes;
			
			// notification title
			$title = 'Mobe - Manutencao Veicular';
					
			// notification message
			$message = "Voce tem $qtd_manutencoes_proximas man prox e $qtd_manutencoes_atrasadas man atrasadas";
			
			// push type - single user / topic
			$push_type = "individual";
			
			// whether to include to image or not
			$include_image = FALSE;
	
	
			$push->setTitle($title);
			$push->setMessage($message);
			if ($include_image) {
				$push->setImage('http://api.androidhive.info/images/minion.jpg');
			} else {
				$push->setImage('');
			}
			$push->setIsBackground(FALSE);
			$push->setPayload($payload);
	
	
			$json = '';
			$response = '';
	
			if ($push_type == 'topic') {
				$json = $push->getPush();
				$response = $firebase->sendToTopic('global', $json);
			} else if ($push_type == 'individual') {
				$json = $push->getPush();
				//$regId = "eXBcZXKUWoU:APA91bEa6GABpYK45LUOncMJg0WS5Gu7TJcIZyCRJqflVfqwhBQPIlM6qPdCnQSesqAVzhH0si-VckmWUxwLy51nV5FymaCtJcNVEeoIGNV5O7lb9BO3tsDG1CDGj3ywmrdXfDHnBXtDDD99HkcMcz9V6c50CFUHeQ";
				$response = $firebase->send($reg_id, $json);
			}
			
		}
						
		return $manutencoes;
		//return $reg_id;
		//return $manutencoes;
		
	}
	
	
	public function gravaRedIdFirebaseDoUsuario($email_usuario, $reg_id_firebase) {
		
		$stmt = $this->conn->prepare("UPDATE tb_usuario SET S_FIREBASE_REG_ID = ? WHERE S_EMAIL = ?");
		$stmt->bind_param("ss", $reg_id_firebase, $email_usuario);
		
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        else {
			$stmt->close();
            return false;
        } 		
		
	}
	
	public function removeRegIdFirebaseDoUsuario($email_usuario) {
		
		$stmt = $this->conn->prepare("UPDATE tb_usuario SET S_FIREBASE_REG_ID = NULL WHERE S_EMAIL = ?");
		$stmt->bind_param("s", $email_usuario);
		
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        else {
			$stmt->close();
            return false;
        } 	
		
	}
	
	
	public function obtemManutencoesDoVeiculo($id_veiculo_usuario) {
		
		$stmt = $this->conn->prepare("SELECT ID, S_DESCRICAO, N_LIMITE_KM, N_LIMITE_TEMPO_MESES, N_KM_ANTECIPACAO,
									 N_TEMPO_ANTECIPACAO_MESES, D_DATA_ULTIMA_MANUTENCAO, N_KM_ULTIMA_MANUTENCAO FROM tb_manutencao_do_veiculo
									 WHERE ID_VEICULO_DO_USUARIO = ?");
		$stmt->bind_param("i", $id_veiculo_usuario);
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				$manutencoes = array();				
				while($linha = $result->fetch_assoc()) {
					
					$manutencao = array();
					$manutencao["id"] = $linha["ID"];
					$manutencao["descricao"] = $linha["S_DESCRICAO"];
					$manutencao["limite_km"] = $linha["N_LIMITE_KM"];
					$manutencao["limite_tempo_meses"] = $linha["N_LIMITE_TEMPO_MESES"];
					$manutencao["km_antecipacao"] = $linha["N_KM_ANTECIPACAO"];
					$manutencao["tempo_antecipacao_meses"] = $linha["N_TEMPO_ANTECIPACAO_MESES"];
					$manutencao["data_ultima_manutencao"] = $linha["D_DATA_ULTIMA_MANUTENCAO"];
					$manutencao["km_ultima_manutencao"] = $linha["N_KM_ULTIMA_MANUTENCAO"];
					array_push($manutencoes,$manutencao);
				}
				$stmt->close();				
				return $manutencoes;
										
			} else {
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}		
	}
	
	public function modificaDispositivoDoVeiculo($codigo_dispositivo, $placa) {
		
		$id_dispositivo = $this->obtemIDDispositivoPeloCodigo($codigo_dispositivo);			
		$stmt = $this->conn->prepare("UPDATE tb_veiculo_do_usuario SET ID_DISPOSITIVO = ? WHERE S_PLACA = ?");
		$stmt->bind_param("is",$id_dispositivo, $placa);
		
		if($stmt->execute()) {
			
			$stmt->close();			
			return true;
		
		} else {
			
			$stmt->close();			
			return false;
			
		}
						
		
	}
	
	public function somaKmDoVeiculo($km, $codigo_dispositivo) {
		
		
		$id_dispositivo = $this->obtemIDDispositivoPeloCodigo($codigo_dispositivo);
		
		$stmt = $this->conn->prepare("UPDATE tb_veiculo_do_usuario SET N_KM = N_KM + ? WHERE ID_DISPOSITIVO = ?");
		$stmt->bind_param("ds", $km, $id_dispositivo);
		
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        else {
			$stmt->close();
            return false;
        } 
				
	}
	
	public function gravaKmRecebidaNoLog($codigo_dispositivo, $km, $host) {
		
		
		$id_dispositivo = $this->obtemIDDispositivoPeloCodigo($codigo_dispositivo);
		$stmt = $this->conn->prepare("INSERT INTO tb_km_recebida(ID_DISPOSITIVO, D_DATA_HORA_RECEBIDA, N_KM, S_HOST) VALUES( ?, NOW(), ?, ?)");
		$stmt->bind_param("sds",$id_dispositivo, $km, $host);
		
		if($stmt->execute()) {
			
			$stmt->close();
			return true;
		
		} else {
			
			$stmt->close();
			return flase;
		}

	}
	
	
	public function mostraInfoVeiculo($placa, $id_usuario) {

		$stmt = $this->conn->prepare("SELECT a.ID, a.D_ANO, a.S_PLACA, a.N_KM, b.S_CODIGO, c.S_MODELO, d.S_MARCA FROM tb_veiculo_do_usuario AS a
									 INNER JOIN tb_dispositivo AS b ON a.ID_DISPOSITIVO = b.ID
									 INNER JOIN tb_modelo_de_veiculo AS c ON a.ID_MODELO_VEICULO = c.ID
									 INNER JOIN tb_marca_de_veiculo AS d ON c.ID_MARCA = d.ID WHERE a.ID_USUARIO = ? AND a.S_PLACA = ?");
		$stmt->bind_param("ss", $id_usuario, $placa);
			
		if($stmt->execute()) {
			
			$veiculo = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			return $veiculo;
		
		} else {
			
			return false;
			
		}				
	}
	
	public function mostraInfoManutencaoDoVeiculo($id_manutencao_do_veiculo) {

		$stmt = $this->conn->prepare("SELECT ID, S_DESCRICAO, N_LIMITE_KM, N_LIMITE_TEMPO_MESES, N_KM_ANTECIPACAO, N_TEMPO_ANTECIPACAO_MESES, D_DATA_ULTIMA_MANUTENCAO, N_KM_ULTIMA_MANUTENCAO
									 FROM tb_manutencao_do_veiculo WHERE ID = ?");
		$stmt->bind_param("i", $id_manutencao_do_veiculo);
			
		if($stmt->execute()) {
			
			$manutencao = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			return $manutencao;
		
		} else {
			
			return false;
			
		}				
	}	
	
	
	public function enviaNovaSenha($email) {
		
		$senha_nova = substr(time(),0,8);
		$senha_nova_criptografada = $this->criptoSenha($senha_nova);
		
		if(smtpmailer($email, 'guilhermerodrigues73@gmail.com', 'Projeto Carro', 'Sua nova senha','Sua nova senha e: '.$senha_nova )) {
                    
			$stmt = $this->conn->prepare("UPDATE tb_usuario SET S_SENHA = ? WHERE S_EMAIL = ?");
            $stmt->bind_param("ss", $senha_nova_criptografada, $email);
            $result = $stmt->execute();
            $stmt->close();
            return true;
			
        } else {
			 
			return false;
			 
		}							
	}
	
	
	public function atualizaSenhaUsuario($email, $senha_antiga, $senha_nova) {
		
		$usuario = $this->obtemUsuarioPorEmailESenha($email, $senha_antiga);
		
		if($usuario) {
			
			$senha_nova_criptografada = $this->criptoSenha($senha_nova);			
			$stmt = $this->conn->prepare("UPDATE tb_usuario SET S_SENHA = ? WHERE S_EMAIL = ?");
			$stmt->bind_param("ss", $senha_nova_criptografada, $email);
			$stmt->execute();
			$stmt->close();
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	public function ativaUsuario($email) {
		
		$stmt = $this->conn->prepare("UPDATE tb_usuario SET B_INATIVO = 0 WHERE S_EMAIL = ?");
		$stmt->bind_param("s",$email);
		
		if($stmt->execute()) {
			
			$stmt->close();
			return true;
			
		} else {
			
			return false;
			
		}
	}
	
	public function inativaUsuario($email) {
		
		$stmt = $this->conn->prepare("UPDATE tb_usuario SET B_INATIVO = 1 WHERE S_EMAIL = ?");
		$stmt->bind_param("s",$email);
		
		if($stmt->execute()) {
			
			smtpmailer($email, 'guilhermerodrigues73@gmail', 'Projeto Carro', 'Desativacao de conta', 'Sua conta foi desativada com sucesso, caso necessite ativar novamente acesse o app.');
			$stmt->close();
			return true;
			
		} else {
			
			return false;
			
		}
	}
	
	public function modificaKmManualmente($placa, $km) {
		
		$stmt = $this->conn->prepare("UPDATE tb_veiculo_do_usuario SET N_KM = ? WHERE S_PLACA = ?");
		$stmt->bind_param("ss", $km, $placa);
		
		if($stmt->execute()) {
			
			$stmt->close();
			return true;
		
		} else {
			
			return false;
		
		}			
		
	}
	
	public function inserirManutencaoPadraoManualmente() {
		
		$s_nome = "Rodizio de pneus";
		$n_limite_km = 10000;
		$n_limite_tempo_meses = 14;
		
		$cont = 2287;
		
		while ($cont <= 2370) {
			
			$stmt = $this->conn->prepare("INSERT INTO tb_manutencao_padrao(S_NOME, ID_MODELO_VEICULO, N_LIMITE_KM, N_LIMITE_TEMPO_MESES) VALUES(?, ?, ?, ?)");
			$stmt->bind_param("siss", $s_nome, $cont, $n_limite_km, $n_limite_tempo_meses);
			$stmt->execute();
			$stmt->close();
			$cont++;
			
		}
		
	}
	
	public function excluiVeiculo($placa) {
		
		$stmt = $this->conn->prepare("DELETE FROM tb_veiculo_do_usuario WHERE S_PLACA = ?");
		$stmt->bind_param("s",$placa);
		
		if($stmt->execute()) {
			
			$stmt->close();
			return true;
		} else {
			return false;
		}	
	}
	
	public function excluiManutencaoDoVeiculo($id_manutencao_do_veiculo) {
		
		$stmt = $this->conn->prepare("DELETE FROM tb_manutencao_do_veiculo WHERE ID = ?");
		$stmt->bind_param("i",$id_manutencao_do_veiculo);
		
		if($stmt->execute()) {
			
			$stmt->close();
			return true;
		} else {
			return false;
		}	
	}
	
	
	public function verificaUsuarioInativo($email) {
		
		$stmt = $this->conn->prepare("SELECT * FROM tb_usuario WHERE S_EMAIL = ? AND B_INATIVO = 1");
		$stmt->bind_param("s",$email);
		$stmt->execute();
		$stmt->store_result();
		
		if($stmt->num_rows > 0) {
			
			$stmt->close();
			return true;
			
		} else {
			
			$stmt->close();
			return false;
			
		}
	}
	
	public function criptoSenha($senha){
		$senha_criptografada = md5(md5($senha));
		
		return $senha_criptografada;
	}
	
	
	
	public function obtemMarcasParaSpinner() {
		
		$stmt = $this->conn->prepare("SELECT * FROM tb_marca_de_veiculo");
		$stmt->execute();
		$result = $stmt->get_result();
				
		if(!empty($result)) {
			
			if($result->num_rows > 0) {
				
				$marcas = array();				
				while($linha = $result->fetch_assoc()) {
					$marca = array();					
					$marca["ID"] = $linha["ID"];
					$marca["MARCA"] = $linha["S_MARCA"];
					array_push($marcas,$marca);
				}
				$stmt->close();				
				return $marcas;
										
			} else {
				$stmt->close();
				return false;
			}
			
		} else {
			$stmt->close();
			return false;
		}				
	}
		
	
	public function obtemIDVeiculoDoUsuarioPelaPlaca($placa) {
		
		$stmt = $this->conn->prepare("SELECT ID from tb_veiculo_do_usuario WHERE S_PLACA = ?");
		$stmt->bind_param("s",$placa);
		$stmt->execute();
		$veiculo_do_usuario = $stmt->get_result()->fetch_assoc();
		$stmt->close();		
		return $veiculo_do_usuario['ID'];
				
	}
	
	
	public function obtemModelosPorIDMarcaParaSpinner($id_marca) {
		
		$stmt = $this->conn->prepare("SELECT * FROM tb_modelo_de_veiculo WHERE ID_MARCA = ?");
		$stmt->bind_param("i",$id_marca);
		$stmt->execute();
		$result = $stmt->get_result();
		
		if($result->num_rows > 0) {
			
			$modelos = array();
			
			while($linha = $result->fetch_assoc()) {
				
				$modelo = array();
				$modelo["ID"] = $linha["ID"];
				$modelo["MODELO"] = $linha["S_MODELO"];
				array_push($modelos,$modelo);
				
			}
			
			$stmt->close();
			return $modelos;
		
		} else {
			
			$stmt->close();
			return false;
		
		}
	}
	
	
	public function preencherMarcasNoBD() {
		
		$json_file = file_get_contents("http://fipeapi.appspot.com/api/1/carros/marcas.json");
		
		$json_str = json_decode($json_file);
		
		foreach($json_str as $registro):
			echo 'Nome: ' . $registro->name . ' - id: ' . $registro->id .'<br>';
			
			$marca = $registro->name;
			$id = $registro->id;
			
			$stmt = $this->conn->prepare("INSERT INTO tb_exemplos_marcas(S_MARCA, ID_MARCA) VALUES( ?, ?)");
			$stmt->bind_param("ss", $marca, $id);
			$stmt->execute();			
			
					
		endforeach;
		$stmt->close();
		
	}
	
	
	public function preencherModelosNoBD() {
		
	
		$stmt = $this->conn->prepare("SELECT ID_MARCA from tb_exemplos_marcas");		
		$stmt->execute();
		$result = $stmt->get_result();
		
		$qtd_marcas = $result->num_rows;
		$cont = 1;
		
		$stmt->close();
		
		while($cont < $qtd_marcas) {
			
			$stmt = $this->conn->prepare("SELECT ID_MARCA from tb_exemplos_marcas WHERE ID = ?");
			$stmt->bind_param("i",$cont);
			$stmt->execute();
			
			$result = $stmt->get_result();
			
			$linha = $result->fetch_assoc();
			
			$id_marca = $linha["ID_MARCA"];
	
			$json_file = file_get_contents("http://fipeapi.appspot.com/api/1/carros/veiculos/$id_marca.json");
		
			$json_str = json_decode($json_file);
						
			foreach($json_str as $registro):
				echo 'Nome: ' . $registro->name . ' - id: ' . $registro->id .'<br>';
				
				$modelo = $registro->name;
				$id_modelo = $registro->id;
				
				$stmt = $this->conn->prepare("INSERT INTO tb_exemplos_veiculos(S_MODELO, ID_MODELO, ID_MARCA) VALUES( ?, ?, ?)");
				$stmt->bind_param("sii", $modelo, $id_modelo, $id_marca);
				$stmt->execute();
				$stmt->close();
										
			endforeach;
			
			$cont++;
				
		}
	}
	
}