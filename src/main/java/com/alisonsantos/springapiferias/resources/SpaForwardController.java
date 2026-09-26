package com.alisonsantos.springapiferias.resources;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// As rotas do front (React Router) nao sao arquivos de verdade - so existem
// no navegador. Quando alguem acessa /minhas-ferias direto (recarrega a
// pagina, ou digita a URL), o Spring precisa devolver o index.html e deixar
// o React decidir o que mostrar, em vez de dar 404.
//
// Se adicionar uma rota nova no front (App.jsx), adicione ela aqui tambem.
@Controller
public class SpaForwardController {

    @RequestMapping({
            "/",
            "/login",
            "/cadastro",
            "/trocar-senha",
            "/minhas-ferias",
            "/coordenador"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
