package com.herbst.vortexbank.mapper;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.v1.dtos.AccountDTO;
import org.modelmapper.ModelMapper;

public class MapperObject {
    private static ModelMapper modelMapper = new ModelMapper();

    static{
        modelMapper.createTypeMap(Account.class, AccountDTO.class).addMapping(Account::getId, AccountDTO::setUserId);
        modelMapper.createTypeMap(AccountDTO.class, Account.class).addMapping(AccountDTO::getUserId, Account::setId);
    }

    public static<O, D> D objectValue(O origin, Class<D> destination){
        return modelMapper.map(origin, destination);
    }
}
