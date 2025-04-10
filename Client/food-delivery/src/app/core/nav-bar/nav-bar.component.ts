import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'nav-bar-root',
    imports: [RouterLink],
    standalone: true,
    templateUrl: './nav-bar.component.html',
    styleUrl: './nav.bar.component.css'
})
export class NavigationBarComponent{
    isLoggedIn = false;
}