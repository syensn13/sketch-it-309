package SS_7.sketch_it.Controllers;

import SS_7.sketch_it.Repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DataAnalysisController {

    @Autowired
    private MongoRepository mongoRepository;


    @RequestMapping(value = "/dataSend", method = RequestMethod.POST)
    public ResponseEntity dataCollection(@RequestBody ArrayList<HashMap<Object, Object>> data) {
        mongoRepository.sendDataAnalysisData(data);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/analytics", method = RequestMethod.GET)
    public String getAnalyticsPage() {
        return "analytics";
    }

    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResponseEntity getData(@RequestParam(name="firstDate",required = false)String firstDate, @RequestParam(name = "secondDate",required = false)String secondDate) throws Exception {
        try {
            if( secondDate!= null  && secondDate.equalsIgnoreCase("null")){
                secondDate = null;
            }
            return new ResponseEntity(mongoRepository.aggregateData(firstDate, secondDate), HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

}
