package dev.sonnyjon.sfgrestdocsexample.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sonnyjon.sfgrestdocsexample.domain.Beer;
import dev.sonnyjon.sfgrestdocsexample.repositories.BeerRepository;
import dev.sonnyjon.sfgrestdocsexample.web.model.BeerDto;
import dev.sonnyjon.sfgrestdocsexample.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "dev.sonnyjon.sfgrestdocsexample.web.mappers")
class BeerControllerTest
{
    public static final String BEER_PATH_V1 = "/api/v1/beer/";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerRepository beerRepository;

    @Test
    void getBeerById() throws Exception
    {
        final String PATH = BEER_PATH_V1 + "{beerId}";

        given(beerRepository.findById(any())).willReturn(Optional.of( Beer.builder().build() ));

        mockMvc.perform(get( PATH, UUID.randomUUID())
                        .accept( MediaType.APPLICATION_JSON ))
                .andExpect( status().isOk() )
                .andDo(document( "v1/beer", pathParameters(
                        parameterWithName( "beerId" ).description( "UUID of desired beer to get." )
                )));
    }

    @Test
    void saveNewBeer() throws Exception
    {
        BeerDto beerDto =  getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString( beerDto );

        mockMvc.perform(post( BEER_PATH_V1 )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( beerDtoJson ))
                .andExpect( status().isCreated() );
    }

    @Test
    void updateBeerById() throws Exception
    {
        BeerDto beerDto =  getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString( beerDto );

        mockMvc.perform(put( BEER_PATH_V1 + UUID.randomUUID() )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( beerDtoJson ))
                .andExpect( status().isNoContent() );
    }

    BeerDto getValidBeerDto()
    {
        return BeerDto
                    .builder()
                    .beerName( "Nice Ale" )
                    .beerStyle( BeerStyleEnum.ALE )
                    .price(new BigDecimal( "9.99" ))
                    .upc( 123123123123L )
                    .build();

    }

}