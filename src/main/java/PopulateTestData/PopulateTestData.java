package PopulateTestData;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.gateways.BankPaymentGateway;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PopulateTestData {

    public static void main(String[] args) {
        String host = "http://34.107.46.200:8080" ;

        List<ArticleCreateDTO> articlesToCreate = new ArrayList<>();
        articlesToCreate.add(new ArticleCreateDTO("banana","Edeka Bio Banane","pc", 1, "Edeka", 0.70, 100));
        articlesToCreate.add(new ArticleCreateDTO("banana","Fairtrade Banane","pc", 5, "None", 1.70, 100));

        articlesToCreate.add(new ArticleCreateDTO("cherry","Kirchliche Kirsche","gr", 200, "Bauernhof Comp", 2.5, 100));
        articlesToCreate.add(new ArticleCreateDTO("cherry","Edeka Kirsche","gr", 500, "Edeka Lokalbauern", 5.10, 100));

        articlesToCreate.add(new ArticleCreateDTO("bell pepper","Gelbe Paprika","pc", 2, "Bauernhof Comp", 1.99, 100));
        articlesToCreate.add(new ArticleCreateDTO("bell pepper","Mixed Paprika","pc", 3, "Bauernhof Comp", 2.49, 100));

        articlesToCreate.add(new ArticleCreateDTO("rice","Langkorn Reis Profikoch","gr", 500, "Profikoch", 1.70, 100));
        articlesToCreate.add(new ArticleCreateDTO("rice","Uncle Bens Reis","gr", 500, "Uncle Ben", 2.70, 100));

        articlesToCreate.add(new ArticleCreateDTO("feta cheese","Grieschicher Feta","gr", 150, "Eww", 1.70, 100));
        articlesToCreate.add(new ArticleCreateDTO("feta cheese","Kreta Feta","gr", 100, "Frisch von der Ziege", 1.30, 100));

        articlesToCreate.add(new ArticleCreateDTO("tomato puree","Tomatenmark","ml", 400, "Noname", 0.70, 100));
        articlesToCreate.add(new ArticleCreateDTO("tomato puree","Tomaten Pueree mit echter Tomate","ml", 400, "Noname", 0.90, 100));

        articlesToCreate.forEach(p -> System.out.println(p));


        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTmpl = new RestTemplate();
        HttpEntity<ArticleCreateDTO> empty = new HttpEntity<>(null, headers);
        restTmpl.postForEntity(host+"/admin/reset", empty, String.class);

        articlesToCreate.forEach(p -> {
            HttpEntity<ArticleCreateDTO> body = new HttpEntity<>(p, headers);
            ResponseEntity<String> entity = restTmpl.postForEntity(host+"/articles", body, String.class);
            System.out.println(entity.getStatusCode().toString());
            System.out.println();
        });

    }
}
