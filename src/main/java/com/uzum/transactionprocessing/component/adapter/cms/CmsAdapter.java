package com.uzum.transactionprocessing.component.adapter.cms;

import com.uzum.transactionprocessing.constant.Constants;
import com.uzum.transactionprocessing.dto.response.CmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE,makeFinal = true)
public class CmsAdapter {

    RestClient restClient;

    public CmsResponse fetchCardInfoByToken(String token){
            return restClient
                    .get()
                    .uri(Constants.cmsUrl + token)
                    .retrieve()
                    .body(CmsResponse.class);
    }

}
