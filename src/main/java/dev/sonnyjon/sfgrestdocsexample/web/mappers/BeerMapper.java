package dev.sonnyjon.sfgrestdocsexample.web.mappers;

import dev.sonnyjon.sfgrestdocsexample.web.model.BeerDto;
import dev.sonnyjon.sfgrestdocsexample.domain.Beer;
import org.mapstruct.Mapper;

/**
 * Created by jt on 2019-05-25.
 */
@Mapper(uses = {DateMapper.class})
public interface BeerMapper {

    BeerDto BeerToBeerDto(Beer beer);

    Beer BeerDtoToBeer(BeerDto dto);
}
