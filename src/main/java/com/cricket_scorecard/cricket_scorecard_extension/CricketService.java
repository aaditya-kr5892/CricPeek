package com.cricket_scorecard.cricket_scorecard_extension;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.time.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CricketService {

    public List<Map<String, Object>> fetchScorecards() throws IOException {
        String url = "https://cricbuzz.com/api/html/homepage-scag";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/125.0.0.0 Safari/537.36")
                .get();

        List<Map<String, Object>> matches = new ArrayList<>();
        Elements cards = doc.select("li.cb-match-card");

        for (Element card : cards) {
            Map<String, Object> match = new HashMap<>();

            // ✅ Safely get link
            Element linkEl = card.selectFirst("a");
            if (linkEl == null) {
                System.out.println("No <a> link found for card: " + card.outerHtml());
                continue;
            }

            String link = linkEl.attr("href");
            String[] parts = link.split("/");
            if (parts.length < 3) {
                System.out.println("Unexpected link format: " + link);
                continue;
            }
            String id = parts[2];
            match.put("id", id);
            match.put("url", "https://www.cricbuzz.com" + link);

            // ✅ Safely get series and format
            Element seriesEl = card.selectFirst(".cb-mtch-crd-hdr");
            Element formatEl = card.selectFirst(".cb-card-match-format");
            match.put("series", seriesEl != null ? seriesEl.text() : "");
            match.put("format", formatEl != null ? formatEl.text() : "");

            // ✅ Team 1
            Element t1NameEl = card.selectFirst(".cb-hmscg-tm-bat-scr .text-normal");
            Elements t1Scores = card.select(".cb-hmscg-tm-bat-scr > .cb-ovr-flo");
            Element t1ScoreEl = t1Scores.size() > 1 ? t1Scores.get(1) : null;
            Map<String, String> t1 = new HashMap<>();
            t1.put("name", t1NameEl != null ? t1NameEl.text() : "");
            t1.put("score", t1ScoreEl != null ? t1ScoreEl.text() : "");
            match.put("team1", t1);

            // ✅ Team 2
            Element t2NameEl = card.selectFirst(".cb-hmscg-tm-bwl-scr .text-normal");
            Elements t2Scores = card.select(".cb-hmscg-tm-bwl-scr > .cb-ovr-flo");
            Element t2ScoreEl = t2Scores.size() > 1 ? t2Scores.get(1) : null;
            Map<String, String> t2 = new HashMap<>();
            t2.put("name", t2NameEl != null ? t2NameEl.text() : "");
            t2.put("score", t2ScoreEl != null ? t2ScoreEl.text() : "");
            match.put("team2", t2);

            // ✅ Status
            Element statusEl = card.selectFirst(".cb-mtch-crd-state");
            Element timingEl = card.selectFirst(".cb-mtch-crd-timing");
            match.put("status", statusEl != null ? statusEl.text() : "");

            Elements timeEls = card.select(".cb-mtch-crd-time");
            String scheduledTime = "";
            Long rawTimestamp = null;

            for (Element timeEl : timeEls) {
                String ngBind = timeEl.attr("ng-bind");
                if (ngBind != null && ngBind.contains("|")) {
                    // Example: 1752663600000 |date: ...
                    String[] prts = ngBind.split("\\|")[0].replaceAll("[^0-9]", "").trim().split(" ");
                    if (prts.length > 0) {
                        try {
                            rawTimestamp = Long.parseLong(prts[0]);

                            // ✅ New: handle timezone, pretty format
                            Instant instant = Instant.ofEpochMilli(rawTimestamp);
                            ZoneId zone = ZoneId.of("Asia/Kolkata");
                            ZonedDateTime matchDateTime = instant.atZone(zone);

                            LocalDate matchDate = matchDateTime.toLocalDate();
                            LocalDate today = LocalDate.now(zone);
                            LocalDate tomorrow = today.plusDays(1);

                            String timePart = matchDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));

                            if (matchDate.equals(today)) {
                                scheduledTime = "Today, " + timePart;
                            } else if (matchDate.equals(tomorrow)) {
                                scheduledTime = "Tomorrow, " + timePart;
                            } else {
                                scheduledTime = matchDateTime.format(DateTimeFormatter.ofPattern("EEEE, d MMM, hh:mm a"));
                            }

                            break; // Found valid timestamp, stop loop
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }

            match.put("scheduledTime", scheduledTime);
            match.put("rawTimestamp", rawTimestamp);


            matches.add(match);
        }

        return matches;
    }

    public List<Map<String, Object>> fetchScores(String matchId) throws Exception {
        String url = "https://cricbuzz.com/api/html/cricket-scorecard/";
        Document doc = Jsoup.connect(url + matchId)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/125.0.0.0 Safari/537.36")
                .get();

        List<Map<String, Object>> inningsList = new ArrayList<>();

        Elements inningsBlocks = doc.select("div[id^=innings_]");

        for (Element inningsBlock : inningsBlocks) {
            Map<String, Object> inningsMap = new LinkedHashMap<>();

            // ✅ Innings name
            Element header = inningsBlock.selectFirst(".cb-scrd-hdr-rw span");
            String inningsName = header != null ? header.text() : "";
            inningsMap.put("inningsName", inningsName);

            // ✅ Batting rows
            List<Map<String, String>> battingList = new ArrayList<>();
            String extras = "";
            String total = "";
            List<String> yetToBat = new ArrayList<>();

            Elements rows = inningsBlock.select(".cb-scrd-itms");
            boolean afterBatting = false;

            for (Element row : rows) {
                Elements cols = row.select(".cb-col");

                // Batting row condition
                if (cols.size() >= 8) {
                    Map<String, String> batter = new LinkedHashMap<>();
                    Elements check = cols.select(".cb-col-25");
                    if(!check.isEmpty()) {
                        batter.put("name", cols.selectFirst("a") != null ? cols.selectFirst("a").text() : "");
                        batter.put("status", cols.get(2).text());
                        batter.put("runs", cols.get(3).text());
                        batter.put("balls", cols.get(4).text());
                        batter.put("fours", cols.get(5).text());
                        batter.put("sixes", cols.get(6).text());
                        batter.put("sr", cols.get(7).text());
                        battingList.add(batter);
                    }

                }
            }
            String t = ((inningsBlock.selectFirst(".cb-col.cb-col-8.text-bold.text-black.text-right").text()!=null) ?
                    inningsBlock.selectFirst(".cb-col.cb-col-8.text-bold.text-black.text-right").text():null);
            Elements left_information = inningsBlock.select(".cb-col-32.cb-col");
            extras = left_information.get(0).text();
            total = t + left_information.get(1).text();
            Element left_batting = ((inningsBlock.select(".cb-col-73.cb-col").first() != null)?inningsBlock.select(".cb-col-73.cb-col").first():null);
            Elements players = null;
            if(left_batting != null)
                players = ((left_batting.select("a")!=null)?left_batting.select("a"):null);
            if(players != null)
                for(Element player : players) {
                    yetToBat.add(player.text());
                }
            inningsMap.put("batting", battingList);
            inningsMap.put("extras", extras);
            inningsMap.put("total", total);
            inningsMap.put("yetToBat", yetToBat);

            // ✅ Bowling rows: same as before
            Element bowlingHeader = inningsBlock.selectFirst(".cb-scrd-sub-hdr.cb-bg-gray:has(div:contains(Bowler))");

            List<Map<String, String>> bowlingList = new ArrayList<>();
            if (bowlingHeader != null) {
                Element next = bowlingHeader.nextElementSibling();
                while (next != null && next.hasClass("cb-scrd-itms")) {
                    Elements cols = next.select(".cb-col");
                    if (cols.size() >= 8) {
                        Map<String, String> bowler = new LinkedHashMap<>();
                        Elements check = cols.select(".cb-col-38");
                        if (!check.isEmpty()) {
                            bowler.put("name", cols.selectFirst("a") != null ? cols.selectFirst("a").text() : "");
                            bowler.put("overs", cols.get(2).text());
                            bowler.put("maidens", cols.get(3).text());
                            bowler.put("runs", cols.get(4).text());
                            bowler.put("wickets", cols.get(5).text());
                            bowler.put("nb", cols.get(6).text());
                            bowler.put("wd", cols.get(7).text());
                            bowler.put("eco", cols.get(8).text());
                            bowlingList.add(bowler);
                        }

                    }
                    next = next.nextElementSibling();
                }
            }

            inningsMap.put("bowling", bowlingList);

            inningsList.add(inningsMap);
        }

        return inningsList;
    }




}
