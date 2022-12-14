package com.spring.junit.exception.movie.JunitExceptionsMovieAssignment.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.junit.exception.movie.JunitExceptionsMovieAssignment.JunitExceptionsMovieAssignmentApplication;
import com.spring.junit.exception.movie.JunitExceptionsMovieAssignment.model.Movie;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringJUnit4ClassRunner.class)   // for running this with junit4
@ContextConfiguration(classes = JunitExceptionsMovieAssignmentApplication.class)  // context setting for the real data (added in main)
@SpringBootTest // spring test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)  // to execute the test methods in order (based on name)
public class MovieControllerTest {

    // For controller based mocks (for web layer)
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext movieContext; // autowired the configuration


    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(movieContext).build();

    }


    // To convert Movie object to JSON content (adding "" and {, } )
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            System.out.println(jsonContent);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





    // THESE test cases check the payload details after the controller URI is called;

    @Test
    public void verifyAllMovies() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/movie/get-movies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());
    }


    @Test
    public void verifyGetMovieById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/movie/get-movie-by-id/636dce61bfff8e1d3db94eff")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("636dce61bfff8e1d3db94eff"))
                .andDo(print());
    }

    @Test
    public void verifyGetMovieById_EXCEPTION() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/movie/get-movie-by-id/636dce61bfff8e1d3db94ef1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.message").value("Movie DOESN'T EXISTS"))
                .andDo(print());
    }





    @Test
    public void verfiySaveMovie() throws Exception{
        Movie movie = new Movie(new ObjectId("636dcf444b7e8832baeb2607"), "MI1", "2002-10-10");

        mockMvc.perform(MockMvcRequestBuilders.post("/movie/add-movie")
                        .content(asJsonString(movie))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value("636dcf444b7e8832baeb2607"))
                .andExpect(jsonPath("$.name").value("MI1"))
                .andDo(print());

    }

    @Test
    public void verfiySaveMovie_EXCEPTION() throws Exception{
        Movie movie = new Movie(new ObjectId("636dcf444b7e8832baeb2607"), "MI1", "2002-10-10");

        mockMvc.perform(MockMvcRequestBuilders.post("/movie/add-movie")
                        .content(asJsonString(movie))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.errorCode").value(400))
                .andExpect(jsonPath("$.message").value("PAYLOAD MALFORMED. OBJECT ID MUST NOT BE DEFINED"))
                .andDo(print());
    }



    @Test
    public void verifyUpdateMovie() throws Exception{
        Movie movie = new Movie(new ObjectId("636dcf444b7e8832baeb2607"), "Mission Impossible 1", "2002-10-10");

        mockMvc.perform(MockMvcRequestBuilders.patch("/movie/update-movie")
                        .content(asJsonString(movie))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value("636dcf444b7e8832baeb2607"))
                .andExpect(jsonPath("$.name").value("Mission Impossible 1"))
                .andDo(print());
    }

    @Test
    public void verifyUpdateMovie_EXCEPTION() throws Exception{
        Movie movie = new Movie(new ObjectId("636de2fcaa778b7b8292c069"), "Mission Impossible 1", "2002-10-10");

        mockMvc.perform(MockMvcRequestBuilders.patch("/movie/update-movie")
                        .content(asJsonString(movie))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.message").value("Movie DOESN'T EXISTS"))
                .andDo(print());
    }



    @Test
    public void verifyDeleteById() throws Exception{

//        {
//            "id": "636de2fcaa778b7b8292c786",
//                "name": "Mission Impossible 4",
//                "releaseDate": "2006-01-01"
//        }

        mockMvc.perform(MockMvcRequestBuilders.delete("/movie/delete-movie/636de2fcaa778b7b8292c786")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Successfully Deleted !!"))
                .andDo(print());
    }

    @Test
    public void verifyDeleteById_EXCEPTION() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.delete("/movie/delete-movie/636de2fcaa778b7b8292c061")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.message").value("Movie DOESN'T Exists"))
                .andDo(print());
    }

}
