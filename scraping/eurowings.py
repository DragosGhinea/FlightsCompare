from playwright.sync_api import sync_playwright
from playwright_stealth import stealth_sync
from bs4 import BeautifulSoup


def scrape_flight_data(page, url):
    print(f"Accessing URL: {url}")
    page.goto(url, wait_until="domcontentloaded")

    try:
        page.wait_for_selector('.o-flight-select', timeout=10231)
    except:
        print("No flights found")
        return []

    page.wait_for_timeout(2314) # Flights load slower than the section holding them

    content = page.content()
    
    soup = BeautifulSoup(content, 'html.parser')

    flight_cards = soup.find_all('div', class_='o-flight-card')

    # List to store flight information dictionaries
    flights_info = []

    # Loop through each flight card
    for card in flight_cards:
        flight_info = {}

        # Extract origin and destination
        origin = card.find('div', class_='o-flight-item__text').text.strip()
        destination = card.find_all('div', class_='o-flight-item__text')[-1].text.strip()

        # Extract times for departure and arrival
        times = card.find_all('div', class_='o-flight-item__title')
        departure_time = times[0].text.strip()
        arrival_time = times[-1].text.strip()

        # Extract stop information
        stop_info = card.find('div', class_='o-flight-item__text-s').text.strip()

        # Extract flight fare details
        fares = []
        fare_items = card.find_all('article', class_='flight-tariff')
        for fare in fare_items:
            fare_type = fare.find('h4', class_='flight-tariff-module__type--XiCoI').text.strip()

            fare_price_element = fare.find('span', class_='farePriceSelect-module__priceVal--lBLXC')
            if fare_price_element:
                fare_price = fare_price_element.text.strip()
            else:
                fare_price = "N/A"

            fare_availability_element = fare.find('p', class_='farePriceSelect-module__availability--qLGXV')
            if fare_availability_element:
                fare_availability = fare_availability_element.text.strip()
            else:
                fare_availability = "N/A"
            
            fares.append({
                'type': fare_type,
                'price': fare_price,
                'availability': fare_availability
            })

        flight_info['origin'] = origin
        flight_info['destination'] = destination
        flight_info['departure_time'] = departure_time
        flight_info['arrival_time'] = arrival_time
        flight_info['stop_info'] = stop_info
        flight_info['fares'] = fares

        # Add the current flight's info to the list
        flights_info.append(flight_info)

    return flights_info


def scrape_flight_data_on_dates(dates, _from, _to):
    data = {}

    with sync_playwright() as p:
        browser = p.webkit.launch(headless=True)
        context = browser.new_context()
        page = context.new_page()
        stealth_sync(page)

        for date in dates:
            year, month, day = date

            url = (f'https://www.eurowings.com/en/booking/flights/flight-search.html'
                    f'?isReward=false&destination={_to}&origin={_from}'
                    f'&source=web&origins={_from}&fromdate={year}-{month}-{day}'
                    f'&triptype=oneway&adults=1&children=0&infants=0&lng=en-GB#/book-flights/select')
            
            flight_data = scrape_flight_data(page, url)
            data[f"{year}-{month}-{day}"] = flight_data

    return data


airports = [
    ("Birmingham", "BHX"),
    ("Bristol", "BRS"),
    ("Edinburgh", "EDI"),
    ("Leeds Bradford", "LBA"),
    ("London Stansted", "STN"),
    ("Manchester", "MAN"),
    ("Madrid", "MAD"),
    ("Málaga", "AGP"),
    ("Palma de Mallorca", "PMI"),
    ("Bologna", "BLQ"),
    ("Catania", "CTA"),
    ("Tel Aviv", "TLV"),
    ("Dublin", "DUB"),
    ("Amman (Jordan)", "AMM"),
    ("Malta", "MLA"),
    ("Chania (Crete)", "CHQ"),
    ("Corfu", "CFU"),
    ("Skiathos", "JSI"),
    ("Thessaloniki", "SKG"),
    ("Genoa", "GOA"),
    ("Berlin", "BER"),
    ("Marseille", "MRS"),
    ("Paris Beauvais", "BVA"),
    ("Milan (Linate)", "LIN"),
    ("Paphos", "PFO"),
    ("Zadar", "ZAD"),
    ("Brussels Charleroi", "CRL"),
    ("Tirana", "TIA"),
    ("Vienna", "VIE"),
    ("Milan Malpensa", "MXP"),
    ("Milan Bergamo", "BGY"),
    ("Naples (Napoli)", "NAP"),
    ("Palermo", "PMO"),
    ("Perugia", "PEG"),
    ("Pescara", "PSR"),
    ("Pisa", "PSA"),
    ("Rome Ciampino", "CIA"),
    ("Venice Treviso", "TSF")
]

def main():
    _from = 'OTP'
        
    for _, _to in airports:
        flights_data = scrape_flight_data_on_dates([(2024, 12, 13)], _from, _to)
        print(flights_data)


if __name__ == "__main__":
    main()
