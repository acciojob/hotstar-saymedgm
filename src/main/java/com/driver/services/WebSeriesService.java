package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same

        if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName())!=null){
            throw new Exception("Series is already present");
        }
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        double newRating  = (productionHouse.getRatings()+webSeriesEntryDto.getRating())/(productionHouse.getWebSeriesList().size()+1);

        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(),webSeriesEntryDto.getAgeLimit(),webSeriesEntryDto.getRating(),webSeriesEntryDto.getSubscriptionType());

        productionHouse.getWebSeriesList().add(webSeries);
        productionHouse.setRatings(newRating);

        webSeries.setProductionHouse(productionHouse);
        webSeries = webSeriesRepository.save(webSeries); //Yahan prr aisa krna hai ki no matter what chahe object return krra ya na....humein dono case
        //handle krne hein
        productionHouseRepository.save(productionHouse);

        //Yahan prr also we need to check if the guy has set the new Ratings assetion mein

        return webSeries.getId();
    }

}