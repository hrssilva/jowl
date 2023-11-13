package com.souslesens.Jowl.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import com.souslesens.Jowl.services.OntopService;
import com.souslesens.Jowl.model.ontopSparqlToSql;
import com.souslesens.Jowl.model.ontopSparqlToSqlInput;

import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;


@RestController
@RequestMapping("/sparql2sql")
public class OntopController {
    
    @Autowired
        OntopService ontopService;

    ontopSparqlToSql sparql2sqlModel;

    @PostMapping("/")
    public ResponseEntity<?> sparql2sql(@RequestBody ontopSparqlToSqlInput request) {

        String reqEncoded64 = request.getSparqlReqEncoded64();
        String repoURI = OntopService.decodeBase64(request.getOntologyURIEncoded64());

        // Check for HealthCheck request
        if (repoURI == "http://check.com/healthcheck") {
            String res = ontopService.healthCheck(sparql2sqlModel.getRepos());
            return ResponseEntity.ok(res);
        }

        OntopRepository repo = sparql2sqlModel.getRepo(repoURI);

        // Add parameter count check?

        if (repo == null) {
            //  create repo - OntopService.ontopInitRepo(ontopSparqlToSqlInput input);
            repo = ontopService.ontopInitRepo(request);
            // Add repo do hash
            sparql2sqlModel.addRepo(repoURI, repo);
        } 

        //  create sql request - OntopService.ontopSPARQL2SQL(String reqEncoded64, OntopRepository repo);
        String response = ontopService.ontopSPARQL2SQL(reqEncoded64, repo);

        return ResponseEntity.ok(response);

    }

    private int countParams(Object... parameters) {
        int count = 0;
        for (Object param : parameters) {
            if (param != null && !param.toString().isEmpty()) {
                count++;
            }
        }
        return count;
    }

}
