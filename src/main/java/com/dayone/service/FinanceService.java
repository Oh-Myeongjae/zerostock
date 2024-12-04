package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

//    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult  getDividendByCompanyName(String companyName){
        // 1. 회사명으로 회사 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 조회된 회사의 id로 배당금 조회
        List<DividendEntity> dividendEntities =  this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 회사와 배당금 정보를 반환
/*
        for (var entity : dividendEntities) {
            dividends.add(Dividend.builder()
                    .date(entity.getDate())
                    .dividend(entity.getDividend())
                    .build());
        }
*/
        // 위의 for문 말고 스트림을 이용할 수도 있음
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        return  new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build()
                , dividends
        );    // 엔티티를 모델로 바꾼다.
    }
}
