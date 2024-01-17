package com.example.plantpilot.Services.Api;

import java.util.ArrayList;

public class PlantApiRequest{
    public PlantApiRequest() {
        this.images = new ArrayList<>();
    }
    public ArrayList<String> images;
    public double latitude;
    public double longitude;
}
