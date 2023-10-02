package com.souslesens.Jowl.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.souslesens.Jowl.services.OntopService;
import com.souslesens.Jowl.model.ontopSparqlToSql;
import com.souslesens.Jowl.model.ontopSparqlToSqlInput;

@RestController
@RequestMapping("/sparql2sql")
public class OntopController {
    //Check repos - ontopSparqlToSql.getRepos();
    //  create repo or  -   OntopService.ontopInitRepo(ontopSparqlToSqlInput input);
    //  create sql request  -   OntopService.ontopSPARQL2SQL(String reqEncoded64, OntopRepository repo);

}
