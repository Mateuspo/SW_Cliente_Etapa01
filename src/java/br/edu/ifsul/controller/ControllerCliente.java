/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsul.controller;

import br.edu.ifsul.servicos.correios.CResultado;
import br.edu.ifsul.servicos.correios.CalcPrecoPrazoWS;
import br.edu.ifsul.servicos.servico.Cliente;
import br.edu.ifsul.servicos.servico.ServicoClienteService;
import br.edu.ifsul.util.Util;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
/**
 *
 * @author Mateus de Oliveira
 */
@Named(value = "controllerCliente")
@SessionScoped
public class ControllerCliente implements Serializable{
    
    private List<Cliente> lista = new ArrayList<>();    
    private Cliente cliente;
    private Boolean editando;
    
    private ServicoClienteService clienteDao;
    private CalcPrecoPrazoWS correios;
    
    public ControllerCliente(){
        clienteDao = new ServicoClienteService();
        correios = new CalcPrecoPrazoWS();
        editando = false;
    }
    
    public String listar(){
        editando = false;
        return "index?faces-redirect=true";
    }
    
    public String novo(){
        cliente = new Cliente();
        cliente.setId(0);
        editando = true;
        return "/Compras/formulario?faces-redirect=true";
    }
    
    public String alterar(Cliente cliente){
        this.cliente = cliente;
        return "/Compras/formulario?faces-redirect=true";
    }
    
    public void excluir(Integer id){
        clienteDao.getServicoClientePort().remover(id);
        Util.mensagemInformacao("O registro foi eliminado com sucesso!");
    }
    
    public String salvar(){
        cliente.setCepOrigem(cliente.getCepOrigem().replace("-", ""));
        cliente.setCepDestino(cliente.getCepDestino().replace("-", ""));
        
        if(cliente.getId() == 0){
            clienteDao.getServicoClientePort().inserir(cliente);
            Util.mensagemInformacao("O registro foi persistido com sucesso!");
        }else{
            clienteDao.getServicoClientePort().alterar(cliente);
            Util.mensagemInformacao("O registro foi atualizado com sucesso!");
        }        
        return "index?faces-redirect=true";
    }

    public List<Cliente> getLista() {
        this.lista = clienteDao.getServicoClientePort().listaClientes();
        return this.lista;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Boolean getEditando() {
        return editando;
    }

    public void setEditando(Boolean editando) {
        this.editando = editando;
    }
    
    public void calcular(){
        this.retornaData();
        this.retornaPreco();
    }
    
    public void retornaData(){
        
        CResultado retorno = correios.getCalcPrecoPrazoWSSoap()
                .calcPrazo(String.valueOf(cliente.getTipoFrete()), 
                        cliente.getCepOrigem(), 
                        cliente.getCepDestino());
        
        cliente.setPrazoEntrega(retorno.getServicos().getCServico().get(0).getPrazoEntrega());
        
    }
    
    public void retornaPreco(){
        CResultado retorno = correios.getCalcPrecoPrazoWSSoap()
                .calcPreco("", "", String.valueOf(cliente.getTipoFrete()), cliente.getCepOrigem(), 
                        cliente.getCepDestino(), cliente.getPeso().toString(), cliente.getFormato(), 
                        cliente.getComprimento(), cliente.getAltura(), cliente.getLargura(), 
                        cliente.getDiametro(), cliente.getMaoPropria(), BigDecimal.valueOf(cliente.getValorDeclarado()), 
                        cliente.getAvisoRecebimento());
        
        if(!retorno.getServicos().getCServico().get(0).getErro().isEmpty()){
            Util.mensagemErro(retorno.getServicos().getCServico().get(0).getMsgErro());
        }else{
            Util.mensagemInformacao("Os dados foram atualizadas com sucesso!");
        }
        
        cliente.setValFrete(Double.parseDouble(retorno.getServicos().getCServico().get(0).getValor().replace(",", ".")));
        cliente.setValTotal(cliente.getValCompra()+ cliente.getValFrete());
        
    }    
}
