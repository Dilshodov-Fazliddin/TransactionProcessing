package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.constant.Constants;
import com.uzum.transactionprocessing.service.CalculateFeeService;
import org.springframework.stereotype.Service;

@Service
public class CalculateFeeServiceImpl implements CalculateFeeService {

    @Override
    public Long calculateFee(Long amount) {
        return amount * Constants.feeAmount / 100;
    }
}
