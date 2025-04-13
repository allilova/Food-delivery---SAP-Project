import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'home-root',
    imports: [RouterLink],
    standalone: true,
    templateUrl: './home.component.html',
    styleUrl: './home.component.css'
})

export class HomeComponent{

}