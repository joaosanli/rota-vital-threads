# language: pt
Funcionalidade: Recomendar fornecedor para um pedido sintético
  Como Mariana, analista de abastecimento,
  quero recomendar um único fornecedor para um pedido sintético,
  para solicitar uma reserva rastreável, seguindo a política de estoque e distância.

  Cenário: Penalizar estoque baixo e descartar estoque insuficiente
    Dado um pedido urgente de 10 unidades do insumo I-01
    E que H-01 oferece 8 unidades a 5 km
    E que H-02 oferece 15 unidades a 10 km
    E que H-03 oferece 30 unidades a 30 km
    Quando a alocação ranquear as ofertas e iniciar uma tentativa
    Então a tentativa ativa deve selecionar H-03 com pontuação 30
    E deve registrar uma solicitação de reserva de 10 unidades para H-03
    E a alocação deve ficar em RESERVA_PENDENTE

  Cenário: Não confirmar enquanto o preparo não terminar
    Dado uma alocação em RESERVA_PENDENTE para H-03
    Quando o Estoque confirmar a reserva
    Então a alocação deve ficar em PREPARO_PENDENTE
    E a alocação não deve estar CONFIRMADA

  Cenário: Compensar a reserva quando o preparo falha
    Dado uma alocação em RESERVA_PENDENTE para H-03
    Quando o Estoque confirmar a reserva
    E o preparo da expedição falhar
    E o Estoque liberar a reserva
    Então a alocação deve ficar em ABERTA
    E a tentativa anterior deve estar ENCERRADA

  Cenário: Registrar SEM_CANDIDATO quando nenhuma oferta é suficiente
    Dado um pedido urgente de 50 unidades do insumo I-01
    E que H-01 oferece 8 unidades a 5 km
    E que H-02 oferece 15 unidades a 10 km
    Quando a alocação ranquear as ofertas e iniciar uma tentativa
    Então a alocação deve ficar em SEM_CANDIDATO
