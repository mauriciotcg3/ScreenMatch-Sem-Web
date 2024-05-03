package br.com.mauriciofreire.screenmatch.principal;

import br.com.mauriciofreire.screenmatch.model.DadosEpisodio;
import br.com.mauriciofreire.screenmatch.model.DadosSerie;
import br.com.mauriciofreire.screenmatch.model.DadosTemporada;
import br.com.mauriciofreire.screenmatch.model.Episodio;
import br.com.mauriciofreire.screenmatch.service.ConsumoAPI;
import br.com.mauriciofreire.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=a64a6c2a";
    private final String SEASON = "&season=";

    public void exibiMenu(){

        System.out.print("Digite o nome da Serie para busca: ");
        var nomeSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        //System.out.println(dados);

		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1 ; i <=  dados.totalTemporadas(); i++){
			json = consumo.obterDados(
                    ENDERECO + nomeSerie.replace(" ", "+") + SEASON + i + API_KEY);
			DadosTemporada dadosTemporadada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporadada);
		}
		//temporadas.forEach(System.out::println);

        System.out.println("\n--------------------------\n");


        System.out.println("Lista de Episodios: ");
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());


        System.out.println("\n--------------------------\n");


//        System.out.println("Top 10 Episodios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A)" + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação" + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite de 10 " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);


        System.out.println("\n--------------------------\n");

        System.out.println("Todos os Episodios com as Devidas Tmeporadas: ");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);


//        System.out.println("\n--------------------------\n");
//
//        //Busca Episodio pelo nome
//        System.out.print("Digite o nome de um episodio: ");
//        var trechoTitulo = sc.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episodio Encontrado!");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("Episodio não Encontrado!");
//        }
//
//        System.out.println("\n--------------------------\n");

//        //Busca Por data
//        System.out.print("Apartir de que ano você deseja ver os episodios?: ");
//        var ano = sc.nextInt();
//        sc.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episodio: " + e.getTitulo() +
//                                " Data lançamento" + e.getDataLancamento().format(formatador)
//                ));


        System.out.println("\n--------------------------\n");

        //calculando a media das avaliações de cada temporada
        System.out.println("Avaliação de Cada Temporada: ");
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);


        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Media: " + est.getAverage());
        System.out.println("Melhor Episodio: " + est.getMax());
        System.out.println("Pior Episodio: " + est.getMin());
        System.out.println("Quantidade de Episodios: " + est.getCount());


    }
}
