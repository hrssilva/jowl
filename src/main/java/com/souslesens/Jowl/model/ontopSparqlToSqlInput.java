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

public class ontopRepoData {

    private String ontologyURIEncoded64;
    public String getOntologyURIEncoded64() {
        return ontologyURIEncoded64;
    }

    private String ontologyContentEncoded64;
    public String getOntologyContentEncoded64() {
        return ontologyContentEncoded64;
    }

    private String mappingsEncoded64;
    public String getMappingsEncoded64() {
        return mappingsEncoded64;
    }

    public ontopSparqlToSqlInput(String ontologyURIEncoded64, String ontologyContentEncoded64, String mappingsEncoded64) {
        this.ontologyURIEncoded64 = ontologyURIEncoded64;
        this.ontologyContentEncoded64 = ontologyContentEncoded64;
        this.mappingsEncoded64 = mappingsEncoded64;
    }


}
