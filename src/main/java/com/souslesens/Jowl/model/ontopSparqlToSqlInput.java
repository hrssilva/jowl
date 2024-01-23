package com.souslesens.Jowl.model;

public class ontopSparqlToSqlInput {
    private String sparqlReqEncoded64;
    public String getSparqlReqEncoded64() {
        return sparqlReqEncoded64;
    }

    private String ontologyURIEncoded64;
    public String getOntologyURIEncoded64() {
        return ontologyURIEncoded64;
    }

    public ontopSparqlToSqlInput(String sparqlReqEncoded64, String ontologyURIEncoded64) {
        this.sparqlReqEncoded64 = sparqlReqEncoded64;
        this.ontologyURIEncoded64 = ontologyURIEncoded64;
    }
    
}
