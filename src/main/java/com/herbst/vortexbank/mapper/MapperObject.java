package com.herbst.vortexbank.mapper;

import com.herbst.vortexbank.entities.Account;
import com.herbst.vortexbank.entities.Wallet;
import com.herbst.vortexbank.v1.dtos.AccountDTO;
import com.herbst.vortexbank.v1.dtos.WalletDTO;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class MapperObject {
    private static ModelMapper modelMapper = new ModelMapper();

    static{
        modelMapper.createTypeMap(Account.class, AccountDTO.class).addMapping(Account::getId, AccountDTO::setAccountId);
        modelMapper.createTypeMap(AccountDTO.class, Account.class).addMapping(AccountDTO::getAccountId, Account::setId);
        modelMapper.createTypeMap(Wallet.class, WalletDTO.class).addMapping(Wallet::getId, WalletDTO::setWalletId);
        modelMapper.createTypeMap(WalletDTO.class, Wallet.class).addMapping(WalletDTO::getWalletId, Wallet::setId);

    }

    public static<O, D> D objectValue(O origin, Class<D> destination){
        return modelMapper.map(origin, destination);
    }

    public static<O, D> List<D> objectsValues(List<O> origin, Class<D> destination){
        return origin.stream().map((entity) -> modelMapper.map(entity, destination)).collect(Collectors.toList());
    }

}
