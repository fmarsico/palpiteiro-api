package com.caravela21.palpiteiro.api.infrastructure.initializer;

import com.caravela21.palpiteiro.api.enums.MatchPhase;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Initializes the database with the FIFA World Cup 2026 group stage teams and matches.
 * Only runs if the teams table is empty, making it safe to restart the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorldCupDataInitializer implements ApplicationRunner {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (teamRepository.count() > 0) {
            log.info("Teams already initialized, skipping World Cup data initialization.");
            return;
        }

        log.info("Initializing FIFA World Cup 2026 group stage data...");
        Map<String, TeamEntity> teams = createTeams();
        createGroupStageMatches(teams);
        log.info("FIFA World Cup 2026 group stage data initialized successfully. {} teams, {} matches.",
                teamRepository.count(), matchRepository.count());
    }

    // ─── flag URLs from flagcdn.com ───────────────────────────────────────────

    private TeamEntity team(String name, String countryCode) {
        TeamEntity t = new TeamEntity();
        t.setName(name);
        t.setFlagUrl("https://flagcdn.com/w160/" + countryCode + ".png");
        return teamRepository.save(t);
    }

    private Map<String, TeamEntity> createTeams() {
        Map<String, TeamEntity> t = new HashMap<>();

        // Group A
        t.put("USA",       team("Estados Unidos", "us"));
        t.put("PAN",       team("Panamá", "pa"));
        t.put("MAR",       team("Marrocos", "ma"));
        t.put("URU",       team("Uruguai", "uy"));

        // Group B
        t.put("MEX",       team("México", "mx"));
        t.put("JAM",       team("Jamaica", "jm"));
        t.put("COL",       team("Colômbia", "co"));
        t.put("SAU",       team("Arábia Saudita", "sa"));

        // Group C
        t.put("CAN",       team("Canadá", "ca"));
        t.put("ECU",       team("Equador", "ec"));
        t.put("KOR",       team("Coreia do Sul", "kr"));
        t.put("CIV",       team("Costa do Marfim", "ci"));

        // Group D
        t.put("ARG",       team("Argentina", "ar"));
        t.put("CHI",       team("Chile", "cl"));
        t.put("NGA",       team("Nigéria", "ng"));
        t.put("CRO",       team("Croácia", "hr"));

        // Group E
        t.put("BRA",       team("Brasil", "br"));
        t.put("BOL",       team("Bolívia", "bo"));
        t.put("JPN",       team("Japão", "jp"));
        t.put("SUI",       team("Suíça", "ch"));

        // Group F
        t.put("FRA",       team("França", "fr"));
        t.put("VEN",       team("Venezuela", "ve"));
        t.put("CMR",       team("Camarões", "cm"));
        t.put("BEL",       team("Bélgica", "be"));

        // Group G
        t.put("ESP",       team("Espanha", "es"));
        t.put("HON",       team("Honduras", "hn"));
        t.put("AUS",       team("Austrália", "au"));
        t.put("SRB",       team("Sérvia", "rs"));

        // Group H
        t.put("GER",       team("Alemanha", "de"));
        t.put("CRC",       team("Costa Rica", "cr"));
        t.put("POL",       team("Polônia", "pl"));
        t.put("SEN",       team("Senegal", "sn"));

        // Group I
        t.put("POR",       team("Portugal", "pt"));
        t.put("PER",       team("Peru", "pe"));
        t.put("GHA",       team("Gana", "gh"));
        t.put("AUT",       team("Áustria", "at"));

        // Group J
        t.put("ENG",       team("Inglaterra", "gb-eng"));
        t.put("DEN",       team("Dinamarca", "dk"));
        t.put("TUR",       team("Turquia", "tr"));
        t.put("UZB",       team("Uzbequistão", "uz"));

        // Group K
        t.put("NED",       team("Países Baixos", "nl"));
        t.put("TRI",       team("Trinidad e Tobago", "tt"));
        t.put("UKR",       team("Ucrânia", "ua"));
        t.put("EGY",       team("Egito", "eg"));

        // Group L
        t.put("ITA",       team("Itália", "it"));
        t.put("SLV",       team("El Salvador", "sv"));
        t.put("ALG",       team("Argélia", "dz"));
        t.put("TUN",       team("Tunísia", "tn"));

        return t;
    }

    // ─── helper para criar jogo ───────────────────────────────────────────────

    private void match(TeamEntity home, TeamEntity away, OffsetDateTime date) {
        MatchEntity m = new MatchEntity();
        m.setHomeTeam(home);
        m.setAwayTeam(away);
        m.setDate(date);
        m.setPhase(MatchPhase.GROUP_STAGE);
        matchRepository.save(m);
    }

    private OffsetDateTime dt(int day, int month, int hour) {
        return OffsetDateTime.of(2026, month, day, hour, 0, 0, 0, ZoneOffset.UTC);
    }

    // ─── 72 jogos da fase de grupos ──────────────────────────────────────────
    // Cada grupo joga 3 rodadas (6 jogos no total):
    //   Rodada 1: T1 x T2, T3 x T4
    //   Rodada 2: T1 x T3, T2 x T4
    //   Rodada 3: T1 x T4, T2 x T3 (simultâneos dentro do grupo)

    private void createGroupStageMatches(Map<String, TeamEntity> t) {

        // ── GRUPO A: USA, PAN, MAR, URU ─── Jun 11, 15, 22
        match(t.get("USA"), t.get("PAN"), dt(11, 6, 21));
        match(t.get("MAR"), t.get("URU"), dt(11, 6, 18));
        match(t.get("USA"), t.get("MAR"), dt(15, 6, 21));
        match(t.get("PAN"), t.get("URU"), dt(15, 6, 18));
        match(t.get("USA"), t.get("URU"), dt(22, 6, 21));
        match(t.get("PAN"), t.get("MAR"), dt(22, 6, 21));

        // ── GRUPO B: MEX, JAM, COL, SAU ─── Jun 11, 16, 22
        match(t.get("MEX"), t.get("JAM"), dt(11, 6, 15));
        match(t.get("COL"), t.get("SAU"), dt(11, 6, 12));
        match(t.get("MEX"), t.get("COL"), dt(16, 6, 21));
        match(t.get("JAM"), t.get("SAU"), dt(16, 6, 18));
        match(t.get("MEX"), t.get("SAU"), dt(22, 6, 18));
        match(t.get("JAM"), t.get("COL"), dt(22, 6, 18));

        // ── GRUPO C: CAN, ECU, KOR, CIV ─── Jun 12, 16, 23
        match(t.get("CAN"), t.get("ECU"), dt(12, 6, 21));
        match(t.get("KOR"), t.get("CIV"), dt(12, 6, 18));
        match(t.get("CAN"), t.get("KOR"), dt(16, 6, 15));
        match(t.get("ECU"), t.get("CIV"), dt(16, 6, 12));
        match(t.get("CAN"), t.get("CIV"), dt(23, 6, 21));
        match(t.get("ECU"), t.get("KOR"), dt(23, 6, 21));

        // ── GRUPO D: ARG, CHI, NGA, CRO ─── Jun 12, 17, 23
        match(t.get("ARG"), t.get("CHI"), dt(12, 6, 15));
        match(t.get("NGA"), t.get("CRO"), dt(12, 6, 12));
        match(t.get("ARG"), t.get("NGA"), dt(17, 6, 21));
        match(t.get("CHI"), t.get("CRO"), dt(17, 6, 18));
        match(t.get("ARG"), t.get("CRO"), dt(23, 6, 18));
        match(t.get("CHI"), t.get("NGA"), dt(23, 6, 18));

        // ── GRUPO E: BRA, BOL, JPN, SUI ─── Jun 13, 17, 24
        match(t.get("BRA"), t.get("BOL"), dt(13, 6, 21));
        match(t.get("JPN"), t.get("SUI"), dt(13, 6, 18));
        match(t.get("BRA"), t.get("JPN"), dt(17, 6, 15));
        match(t.get("BOL"), t.get("SUI"), dt(17, 6, 12));
        match(t.get("BRA"), t.get("SUI"), dt(24, 6, 21));
        match(t.get("BOL"), t.get("JPN"), dt(24, 6, 21));

        // ── GRUPO F: FRA, VEN, CMR, BEL ─── Jun 13, 18, 24
        match(t.get("FRA"), t.get("VEN"), dt(13, 6, 15));
        match(t.get("CMR"), t.get("BEL"), dt(13, 6, 12));
        match(t.get("FRA"), t.get("CMR"), dt(18, 6, 21));
        match(t.get("VEN"), t.get("BEL"), dt(18, 6, 18));
        match(t.get("FRA"), t.get("BEL"), dt(24, 6, 18));
        match(t.get("VEN"), t.get("CMR"), dt(24, 6, 18));

        // ── GRUPO G: ESP, HON, AUS, SRB ─── Jun 14, 18, 25
        match(t.get("ESP"), t.get("HON"), dt(14, 6, 21));
        match(t.get("AUS"), t.get("SRB"), dt(14, 6, 18));
        match(t.get("ESP"), t.get("AUS"), dt(18, 6, 15));
        match(t.get("HON"), t.get("SRB"), dt(18, 6, 12));
        match(t.get("ESP"), t.get("SRB"), dt(25, 6, 21));
        match(t.get("HON"), t.get("AUS"), dt(25, 6, 21));

        // ── GRUPO H: GER, CRC, POL, SEN ─── Jun 14, 19, 25
        match(t.get("GER"), t.get("CRC"), dt(14, 6, 15));
        match(t.get("POL"), t.get("SEN"), dt(14, 6, 12));
        match(t.get("GER"), t.get("POL"), dt(19, 6, 21));
        match(t.get("CRC"), t.get("SEN"), dt(19, 6, 18));
        match(t.get("GER"), t.get("SEN"), dt(25, 6, 18));
        match(t.get("CRC"), t.get("POL"), dt(25, 6, 18));

        // ── GRUPO I: POR, PER, GHA, AUT ─── Jun 15, 20, 26
        match(t.get("POR"), t.get("PER"), dt(15, 6, 21));
        match(t.get("GHA"), t.get("AUT"), dt(15, 6, 18));
        match(t.get("POR"), t.get("GHA"), dt(20, 6, 21));
        match(t.get("PER"), t.get("AUT"), dt(20, 6, 18));
        match(t.get("POR"), t.get("AUT"), dt(26, 6, 21));
        match(t.get("PER"), t.get("GHA"), dt(26, 6, 21));

        // ── GRUPO J: ENG, DEN, TUR, UZB ─── Jun 15, 20, 26
        match(t.get("ENG"), t.get("DEN"), dt(15, 6, 15));
        match(t.get("TUR"), t.get("UZB"), dt(15, 6, 12));
        match(t.get("ENG"), t.get("TUR"), dt(20, 6, 15));
        match(t.get("DEN"), t.get("UZB"), dt(20, 6, 12));
        match(t.get("ENG"), t.get("UZB"), dt(26, 6, 18));
        match(t.get("DEN"), t.get("TUR"), dt(26, 6, 18));

        // ── GRUPO K: NED, TRI, UKR, EGY ─── Jun 16, 21, 27
        match(t.get("NED"), t.get("TRI"), dt(16, 6, 21));
        match(t.get("UKR"), t.get("EGY"), dt(16, 6, 18));
        match(t.get("NED"), t.get("UKR"), dt(21, 6, 21));
        match(t.get("TRI"), t.get("EGY"), dt(21, 6, 18));
        match(t.get("NED"), t.get("EGY"), dt(27, 6, 21));
        match(t.get("TRI"), t.get("UKR"), dt(27, 6, 21));

        // ── GRUPO L: ITA, SLV, ALG, TUN ─── Jun 16, 21, 27
        match(t.get("ITA"), t.get("SLV"), dt(16, 6, 15));
        match(t.get("ALG"), t.get("TUN"), dt(16, 6, 12));
        match(t.get("ITA"), t.get("ALG"), dt(21, 6, 15));
        match(t.get("SLV"), t.get("TUN"), dt(21, 6, 12));
        match(t.get("ITA"), t.get("TUN"), dt(27, 6, 18));
        match(t.get("SLV"), t.get("ALG"), dt(27, 6, 18));
    }
}

