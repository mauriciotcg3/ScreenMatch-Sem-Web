package br.com.mauriciofreire.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("TotalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao) {


}
