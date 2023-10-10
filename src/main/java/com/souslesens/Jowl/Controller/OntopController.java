package com.souslesens.Jowl.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.souslesens.Jowl.services.OntopService;
import com.souslesens.Jowl.model.ontopSparqlToSql;
import com.souslesens.Jowl.model.ontopSparqlToSqlInput;

@RestController
@RequestMapping("/sparql2sql")
public class OntopController {
    
    @Autowired
        OntopService ontopService;

    @PostMapping("/")
    public ResponseEntity<?> sparql2sql(@RequestBody ontopSparqlToSqlInput request) {

        String reqEncoded64 = request.getSparqlReqEncoded64();
        String repoURI = request.getOntologyURIEncoded64();
        OntopRepository repo = ontopSparqlToSql.getRepo(repoURI);

        // Add parameter count check?

        if (repo == null) {
            //  create repo - OntopService.ontopInitRepo(ontopSparqlToSqlInput input);
            repo = ontopService.ontopInitRepo(request);
            // Add repo do hash
            ontopSparqlToSql.addRepo(repoURI, repo);
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
