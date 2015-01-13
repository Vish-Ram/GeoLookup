curl -XPUT http://localhost:9200/geo_data -d '
{
    "mappings": {
        "area": {
            "properties": {
                "index": {"type": "integer"},
                "zipcode": {"type": "string"},
                "city": {"type": "string"},
                "state": {"type": "string"},
                "location": {"type": "geo_point"}
            }
        }
    }
}
'