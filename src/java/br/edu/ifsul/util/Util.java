/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsul.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Mateus de Oliveira
 */
public class Util {
    
    public static String getMensagemErro(Exception e){
        
        String retorno;
        
        while(e.getCause() != null){
            e = (Exception) e.getCause();
        }
        
        retorno = e.getMessage();
        
        if(retorno.contains("chave estrangeira")){
            retorno = "O registro não pode ser removido por possuir referências em outros objetos";
        }
        
        
        return retorno;
    }
    
    public static void mensagemInformacao(String msg){
        FacesMessage mensagem = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, mensagem);
    }
    
    public static void mensagemErro(String msg){
        FacesMessage mensagem = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, mensagem);
    }
}
