package com.ashish.elasticsearch_project.sec05;

import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.ashish.elasticsearch_project.AbstractTest;
import com.ashish.elasticsearch_project.sec05.entity.Garment;
import com.ashish.elasticsearch_project.sec05.repository.GarmentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;
import java.util.stream.Collectors;

public class NativeAndCriteriaQueryTest extends AbstractTest {

    @Autowired
    private GarmentRepository repository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeAll
    public void dataSetup(){
        var garments = this.readResource("sec05/garments.json", new TypeReference<List<Garment>>() {
        });
        this.repository.saveAll(garments);
        Assertions.assertEquals(20,garments.size());
    }

    @Test
    public void criteriaQuery(){

        var nameIsShirt = Criteria.where("name").is("shirt");
        verify(nameIsShirt,1);

        var priceAbove100 = Criteria.where("price").greaterThan(100);
        verify(priceAbove100,5);

        this.verify(nameIsShirt.or(priceAbove100),6);

        var brandIsZara = Criteria.where("brand").is("Zara");
        this.verify(priceAbove100.and(brandIsZara.not()),3);

        var fuzzyMatchShort = Criteria.where("name").fuzzy("short");
        this.verify(fuzzyMatchShort,1);
    }

    private void verify(Criteria criteria, int expectedResultsCount){
        var query = CriteriaQuery.builder(criteria).build();
        var searchHits = this.elasticsearchOperations.search(query, Garment.class);
        searchHits.forEach(this.print());
        Assertions.assertEquals(expectedResultsCount,searchHits.getTotalHits());
    }

    @Test
    public void boolQuery(){

        var occasionCausal = Query.of(b -> b.term(
                TermQuery.of(tb -> tb.field("occasion").value("Casual"))
        ));

        var colorBrown = Query.of(b -> b.term(
                TermQuery.of(tb -> tb.field("color").value("Brown"))
        ));

        var priceRange = Query.of(b -> b.range(
                RangeQuery.of(tb -> tb.number(
                        NumberRangeQuery.of(nrb -> nrb.field("price").lte(50d))
                ))
        ));

        var boolQuery = Query.of(b -> b.bool(
                BoolQuery.of(bb -> bb.filter(occasionCausal,priceRange).should(colorBrown))
        ));

        var nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .build();

        var searchHits = this.elasticsearchOperations.search(nativeQuery, Garment.class);
        searchHits.forEach(this.print());
        //Assertions.assertEquals(4,searchHits.getTotalHits());
    }

    @Test
    public void aggregation(){
        var priceStats = Aggregation.of(b -> b.stats(
                StatsAggregation.of(sb -> sb.field("price"))
        ));

        var group_by_brand = Aggregation.of(b -> b.terms(
                TermsAggregation.of(tb -> tb.field("field"))
        ));

        var group_by_color = Aggregation.of(b -> b.terms(
                TermsAggregation.of(tb -> tb.field("color"))
        ));

        var price_range = Aggregation.of(b -> b.range(
                RangeAggregation.of(tb -> tb.field("price").ranges(
                        AggregationRange.of(ab -> ab.to(50d)),
                        AggregationRange.of(ab -> ab.from(50d).to(100d)),
                        AggregationRange.of(ab -> ab.from(100d).to(150d)),
                        AggregationRange.of(ab -> ab.from(150d))
                ))
        ));

        var query = NativeQuery.builder()
                .withMaxResults(0)
                .withAggregation("price-stats",priceStats)
                .withAggregation("group-by-brand",group_by_brand)
                .withAggregation("group-by-color",group_by_color)
                .withAggregation("price-range",price_range)
                .build();

        var searchHits = this.elasticsearchOperations.search(query, Garment.class);
        var aggregations = (List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations();

        var map = aggregations.stream()
                .map(ElasticsearchAggregation::aggregation)
                .collect(Collectors.toMap(
                        a -> a.getName(),
                        a -> a.getAggregate()
                ));
        this.print().accept(map);
    }
}
