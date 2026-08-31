UPDATE recipes
SET
    image_url = 'https://res.cloudinary.com/urupul0g/image/upload/v1787066658/coccia-house/weekly-offerings/fhoxzxblweam6etfdsn4.jpg',
    image_public_id = 'coccia-house/weekly-offerings/fhoxzxblweam6etfdsn4'
WHERE LOWER(name) = LOWER('Cannoli');

UPDATE recipes
SET
    image_url = 'https://res.cloudinary.com/urupul0g/image/upload/v1787066607/coccia-house/weekly-offerings/ob4wd5xvlkwem4ck477e.jpg',
    image_public_id = 'coccia-house/weekly-offerings/ob4wd5xvlkwem4ck477e'
WHERE LOWER(name) = LOWER('Chicken Cacciatore');
