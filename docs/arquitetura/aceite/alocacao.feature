# language: pt
Funcionalidade: Recomendar fornecedor para um pedido sintético
  Cenário: Penalizar estoque baixo e descartar estoque insuficiente
    Dado um pedido urgente de 10 unidades do insumo I-01
    E que H-01 oferece 8 unidades a 5 km
    E que H-02 oferece 15 unidades a 10 km
    E que H-03 oferece 30 unidades a 30 km
    Quando a alocação ranquear as ofertas e iniciar uma tentativa
    Então a tentativa ativa deve selecionar H-03 com pontuação 30
    E deve registrar uma solicitação de reserva de 10 unidades para H-03
    E a alocação deve ficar em RESERVA_PENDENTE
