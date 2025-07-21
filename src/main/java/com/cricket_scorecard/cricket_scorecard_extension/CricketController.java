package com.cricket_scorecard.cricket_scorecard_extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
public class CricketController {

    private final CricketService cricketService;


    @Autowired
    public CricketController(CricketService cricketService) {
        this.cricketService = cricketService;
    }


    @GetMapping("/api/scorecard")
    public List<Map<String, Object>> getScorecard() throws IOException {
        return cricketService.fetchScorecards();
    }

    @GetMapping("/api/scorecard/{matchid}")
    public List<Map<String, Object>> matchScorecard(@PathVariable("matchid") String matchid) throws Exception {
        return cricketService.fetchScores(matchid);
    }

    @GetMapping("/test")
    public String test() {
        return "Backend is working!";
    }

}

