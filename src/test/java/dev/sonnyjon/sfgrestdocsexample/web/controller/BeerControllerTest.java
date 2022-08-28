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
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "dev.sonnyjon.sfgrestdocsexample.web.mappers")
class BeerControllerTest
{
    public static final String BEER_PATH_V1 = "/api/v1/beer/";
    public static final String DOC_PATH = "v1/beer";

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
                        .param( "iscold", "yes" )
                        .accept( MediaType.APPLICATION_JSON ))
                .andExpect( status().isOk() )
                .andDo(document( DOC_PATH + "-GET",
                        pathParameters(
                            parameterWithName( "beerId" ).description( "UUID of desired beer to get." )
                        ),
                        requestParameters(
                            parameterWithName( "iscold" ).description( "Is Beer Cold Query param" )
                        ),
                        responseFields(
                            fieldWithPath( "id" ).description( "Id of Beer" ),
                            fieldWithPath( "version" ).description( "Version number" ),
                            fieldWithPath( "createdDate" ).description( "Date Created" ),
                            fieldWithPath( "lastModifiedDate" ).description( "Date Updated" ),
                            fieldWithPath( "beerName" ).description( "Beer Name" ),
                            fieldWithPath( "beerStyle" ).description( "Beer Style" ),
                            fieldWithPath( "upc" ).description( "UPC of Beer" ),
                            fieldWithPath( "price" ).description( "Price" ),
                            fieldWithPath( "quantityOnHand" ).description( "Quantity On hand" )
                        )
                ));
    }

    @Test
    void saveNewBeer() throws Exception
    {
        BeerDto beerDto =  getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString( beerDto );
        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);

        mockMvc.perform(post( BEER_PATH_V1 )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( beerDtoJson ))
                .andExpect( status().isCreated() )
                .andDo(document( DOC_PATH + "-POST",
                        requestFields(
                            fields.withPath( "id" ).ignored(),
                            fields.withPath( "version" ).ignored(),
                            fields.withPath( "createdDate" ).ignored(),
                            fields.withPath( "lastModifiedDate" ).ignored(),
                            fields.withPath( "beerName" ).description( "Name of the beer" ),
                            fields.withPath( "beerStyle" ).description( "Style of Beer" ),
                            fields.withPath( "upc" ).description( "Beer UPC" ).attributes(),
                            fields.withPath( "price" ).description( "Beer Price" ),
                            fields.withPath( "quantityOnHand" ).description( "Quantity On hand" )
                        )
                ));
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

    private static class ConstrainedFields
    {
        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input)
        {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path)
        {
            String value = StringUtils.collectionToDelimitedString(
                    this.constraintDescriptions.descriptionsForProperty( path ),
                    ". "
            );

            return fieldWithPath( path )
                    .attributes(
                            key( "constraints" ).value( value )
                    );
        }
    }
}