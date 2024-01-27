package com.example.plantpilot.Services.Api;

import java.util.ArrayList;

public class Suggestion{
    public String id;
    public String name;
    public double probability;
    public ArrayList<SimilarImage> similar_images;
    public Details details;
}