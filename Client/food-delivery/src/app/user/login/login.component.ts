import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'login-root',
    imports: [RouterLink],
    standalone: true,
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})

export class LoginComponent{

}